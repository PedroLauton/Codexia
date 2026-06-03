package br.com.codexia.identity.domain.model.aggregate;

import br.com.codexia.identity.domain.model.valueobject.ProviderName;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.identity.domain.exception.account.AccountDeletedException;
import br.com.codexia.identity.domain.exception.account.AccountGracePeriodExpiredException;
import br.com.codexia.identity.domain.exception.account.AccountNotDeletedException;
import br.com.codexia.identity.domain.model.entity.LocalCredential;
import br.com.codexia.identity.domain.model.entity.ExternalIdentity;
import br.com.codexia.identity.domain.model.enums.AccountRole;
import br.com.codexia.identity.domain.model.enums.Permission;
import br.com.codexia.identity.domain.model.valueobject.AvatarUrl;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.shared.domain.event.DomainEvent;
import br.com.codexia.identity.domain.event.AccountCreatedEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {

    private final AccountId id;
    private Email email;
    private Name name;
    private AvatarUrl avatarUrl;
    private AccountRole role;
    private LocalCredential localCredential;
    private List<ExternalIdentity> externalIdentities = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // base — inicialização comum para criação
    private Account(Email email, Name name) {
        validate(email, name);
        this.id = AccountId.generate();
        this.email = email;
        this.name = name;
        this.role = AccountRole.USER;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // base — reconstituição do banco
    private Account(AccountId id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static Account createWithCredentials(Email email, Name name, String passwordHash) {
        Account account = new Account(email, name);
        account.localCredential = new LocalCredential(account.id, passwordHash);

        account.registerEvent(new AccountCreatedEvent(
                account.getId(),
                account.getEmail(),
                account.getName()
        ));

        return account;
    }

    public static Account createWithOAuth(Email email, Name name, AvatarUrl avatarUrl, ProviderName providerName, String providerId) {
        Account account = new Account(email, name);
        account.avatarUrl = avatarUrl;
        account.externalIdentities.add(new ExternalIdentity(account.id, providerName, providerId));

        account.registerEvent(new AccountCreatedEvent(
                account.getId(),
                account.getEmail(),
                account.getName()
        ));

        return account;
    }

    public static Account reconstitute(AccountId id, Email email, Name name, AvatarUrl avatarUrl, AccountRole role, LocalCredential localCredential, List<ExternalIdentity> externalIdentities, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        Account account = new Account(id, createdAt);
        account.email = email;
        account.name = name;
        account.avatarUrl = avatarUrl;
        account.role = role;
        account.localCredential = localCredential;
        account.externalIdentities = new ArrayList<>(externalIdentities);
        account.updatedAt = updatedAt;
        account.deletedAt = deletedAt;
        return account;
    }

    public void linkOAuthCredential(ProviderName providerName, String providerId) {
        checkNotDeleted();
        boolean alreadyLinked = hasExternalIdentities(providerName);
        if (alreadyLinked) return;

        externalIdentities.add(new ExternalIdentity(this.id, providerName, providerId));
        this.updatedAt = Instant.now();
    }

    public void addLocalCredential(String passwordHash) {
        checkNotDeleted();
        if (this.localCredential != null)
            throw new IllegalStateException("Account already has a local credential.");
        this.localCredential = new LocalCredential(this.id, passwordHash);
        this.updatedAt = Instant.now();
    }

    public void changeEmail(Email newEmail) {
        checkNotDeleted();
        if (newEmail == null)
            throw new IllegalArgumentException("Email is mandatory.");
        this.email = newEmail;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(Name name, AvatarUrl avatarUrl) {
        checkNotDeleted();
        if (name == null)
            throw new IllegalArgumentException("Name is mandatory.");
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        checkNotDeleted();
        this.deletedAt = Instant.now();
    }

    public void restore(int gracePeriodDays) {
        if (!isDeleted())
            throw new AccountNotDeletedException(this.id);
        if (!isWithinGracePeriod(gracePeriodDays))
            throw new AccountGracePeriodExpiredException(this.id);
        this.deletedAt = null;
        this.updatedAt = Instant.now();
    }

    public void promoteToAdmin() {
        checkNotDeleted();
        this.role = AccountRole.ADMIN;
        this.updatedAt = Instant.now();
    }

    public boolean hasPermission(Permission permission) {
        return this.role.hasPermission(permission);
    }

    public boolean hasLocalCredential() {
        return this.localCredential != null;
    }

    public boolean hasExternalIdentities(ProviderName providerName) {
        return externalIdentities.stream()
                .anyMatch(c -> c.getProvider() == providerName);
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    private boolean isWithinGracePeriod(int gracePeriodDays) {
        Instant expirationDate = deletedAt.plus(gracePeriodDays, ChronoUnit.DAYS);
        return expirationDate.isAfter(Instant.now());
    }

    private void checkNotDeleted() {
        if (isDeleted()) throw new AccountDeletedException(this.id);
    }

    private void validate(Email email, Name name) {
        if (email == null)
            throw new IllegalArgumentException("Email is mandatory.");
        if (name == null)
            throw new IllegalArgumentException("Name is mandatory.");
    }

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    public AccountId getId() { return id; }
    public Email getEmail() { return email; }
    public Name getName() { return name; }
    public AvatarUrl getAvatarUrl() { return avatarUrl; }
    public AccountRole getRole() { return role; }
    public LocalCredential getLocalCredential() { return localCredential; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }

    public List<ExternalIdentity> getExternalIdentities() {
        return Collections.unmodifiableList(externalIdentities);
    }
}
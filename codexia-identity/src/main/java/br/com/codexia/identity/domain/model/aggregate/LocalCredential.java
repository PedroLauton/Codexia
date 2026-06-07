package br.com.codexia.identity.domain.model.aggregate;

import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.domain.model.AccountId;

import java.time.Instant;

public class LocalCredential {

    private final AccountId accountId;
    private PasswordHash passwordHash;
    private final Instant createdAt;
    private Instant updatedAt;

    public LocalCredential(AccountId accountId, PasswordHash passwordHash) {
        if (accountId == null)
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (passwordHash == null)
            throw new IllegalArgumentException("Password hash is mandatory.");

        this.accountId = accountId;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // construtor de reconstituição
    public LocalCredential(AccountId accountId, PasswordHash passwordHash,
                           Instant createdAt, Instant updatedAt) {
        this.accountId = accountId;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    void changePassword(PasswordHash newPasswordHash) {
        if (newPasswordHash == null)
            throw new IllegalArgumentException("New password hash is mandatory.");
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public AccountId getAccountId() { return accountId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

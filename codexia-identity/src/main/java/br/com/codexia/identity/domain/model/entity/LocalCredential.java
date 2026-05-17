package br.com.codexia.identity.domain.model.entity;

import br.com.codexia.shared.domain.model.AccountId;

import java.time.Instant;

public class LocalCredential {

    private final AccountId accountId;
    private String passwordHash;
    private final Instant createdAt;
    private Instant updatedAt;

    public LocalCredential(AccountId accountId, String passwordHash) {
        if (accountId == null)
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (passwordHash == null || passwordHash.isBlank())
            throw new IllegalArgumentException("Password hash is mandatory.");

        this.accountId = accountId;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public LocalCredential(AccountId accountId, String passwordHash,
                           Instant createdAt, Instant updatedAt) {
        this.accountId = accountId;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank())
            throw new IllegalArgumentException("Password hash is mandatory.");
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    public AccountId getAccountId() { return accountId; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

}

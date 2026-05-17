package br.com.codexia.identity.domain.model.entity;

import br.com.codexia.identity.domain.model.enums.IdentityProvider;
import br.com.codexia.shared.domain.model.AccountId;

import java.time.Instant;

public class ExternalIdentity {

    private final AccountId accountId;
    private final IdentityProvider provider;
    private final String providerId;
    private final Instant createdAt;

    public ExternalIdentity(AccountId accountId, IdentityProvider provider,
                            String providerId) {
        if (accountId == null)
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (provider == null)
            throw new IllegalArgumentException("Provider is mandatory.");
        if (providerId == null || providerId.isBlank())
            throw new IllegalArgumentException("ProviderId is mandatory.");

        this.accountId = accountId;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = Instant.now();
    }

    public ExternalIdentity(AccountId accountId, IdentityProvider provider,
                            String providerId, Instant createdAt) {
        this.accountId = accountId;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public AccountId getAccountId() { return accountId; }
    public IdentityProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public Instant getCreatedAt() { return createdAt; }
}

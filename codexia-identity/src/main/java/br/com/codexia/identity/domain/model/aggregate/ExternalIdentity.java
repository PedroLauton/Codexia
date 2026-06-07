package br.com.codexia.identity.domain.model.aggregate;

import br.com.codexia.identity.domain.model.valueobject.ProviderName;
import br.com.codexia.shared.domain.model.AccountId;

import java.time.Instant;

public class ExternalIdentity {

    private final AccountId accountId;
    private final ProviderName providerName;
    private final String providerId;
    private final Instant createdAt;

    public ExternalIdentity(AccountId accountId, ProviderName providerName, String providerId) {
        if (accountId == null)
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (providerName == null)
            throw new IllegalArgumentException("Provider is mandatory.");
        if (providerId == null || providerId.isBlank())
            throw new IllegalArgumentException("ProviderId is mandatory.");

        this.accountId = accountId;
        this.providerName = providerName;
        this.providerId = providerId;
        this.createdAt = Instant.now();
    }

    public ExternalIdentity(AccountId accountId, ProviderName providerName, String providerId, Instant createdAt) {
        this.accountId = accountId;
        this.providerName = providerName;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public AccountId getAccountId() { return accountId; }
    public ProviderName getProvider() { return providerName; }
    public String getProviderId() { return providerId; }
    public Instant getCreatedAt() { return createdAt; }
}

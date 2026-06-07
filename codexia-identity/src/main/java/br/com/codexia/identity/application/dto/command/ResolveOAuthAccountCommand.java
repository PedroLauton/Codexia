package br.com.codexia.identity.application.dto.command;

public record ResolveOAuthAccountCommand(
        String providerName,
        String providerId,
        String email,
        String name,
        String avatarUrl
) {
    public ResolveOAuthAccountCommand {
        if (providerName == null || providerName.isBlank())
            throw new IllegalArgumentException("Provider name is mandatory.");
        if (providerId == null || providerId.isBlank())
            throw new IllegalArgumentException("Provider id is mandatory.");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is mandatory.");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is mandatory.");
    }
}
package br.com.codexia.identity.application.dto.response;

public record OAuthUserInfo(
        String providerName,
        String providerId,
        String email,
        String name,
        String avatarUrl
) {}

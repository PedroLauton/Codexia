package br.com.codexia.identity.application.dto.response;

import java.time.Instant;

public record AccountResponse(
        String id,
        String email,
        String name,
        String avatarUrl,
        String role,
        Instant createdAt
) {}
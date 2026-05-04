package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;

public record TagResponse(
        String id,
        String workspaceId,
        String title,
        String hexColor,
        Instant createdAt,
        Instant updatedAt
) {}
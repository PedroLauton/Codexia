package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;

public record CategoryResponse(
        String id,
        String workspaceId,
        String parentId,
        int depth,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}

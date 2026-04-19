package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;

public record SnippetVersionResponse(
        String id,
        String title,
        String description,
        String content,
        String language,
        Instant createdAt
) {
}

package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;

public record SnippetVersionAddedResponse(
        String snippetId,
        SnippetVersionResponse latestVersion,
        Instant updatedAt
) {}
package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;
import java.util.Set;

public record SnippetReassignedResponse(
        String id,
        String workspaceId,
        String categoryId,
        Set<TagSummaryResponse> tags,
        Instant updatedAt
) {}

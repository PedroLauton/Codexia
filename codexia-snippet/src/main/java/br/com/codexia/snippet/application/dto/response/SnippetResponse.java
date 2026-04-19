package br.com.codexia.snippet.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record SnippetResponse(
        String id,
        String workspaceId,
        String accountId,
        String categoryId,
        Set<TagSummaryResponse> tags,
        List<SnippetVersionResponse> versions,
        Instant createdAt,
        Instant updatedAt
) {
}

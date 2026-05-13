package br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request;

import java.util.Set;

public record ReassignSnippetRequest(
        String categoryId,
        Set<String> tagIds
) {
}

package br.com.codexia.snippet.application.dto.command;

import java.util.Set;

public record CreateSnippetCommand(
        String workspaceId,
        String accountId,
        String categoryId,
        Set<String> tagIds,
        String title,
        String description,
        String content,
        String language
) {
}

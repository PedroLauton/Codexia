package br.com.codexia.snippet.application.dto.command;

public record CreateCategoryCommand(
        String workspaceId,
        String name,
        String description,
        String parentId
) {
}

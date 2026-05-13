package br.com.codexia.snippet.application.dto.command;

public record ReparentCategoryCommand(
        String categoryId,
        String workspaceId,
        String parentId
) {}

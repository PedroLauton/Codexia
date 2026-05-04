package br.com.codexia.snippet.application.dto.command;

public record DeleteCategoryCommand(
        String categoryId,
        String workspaceId
) {}

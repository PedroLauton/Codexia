package br.com.codexia.snippet.application.dto.command;

public record UpdateCategoryCommand(
        String categoryId,
        String workspaceId,
        String name,
        String description
) {}
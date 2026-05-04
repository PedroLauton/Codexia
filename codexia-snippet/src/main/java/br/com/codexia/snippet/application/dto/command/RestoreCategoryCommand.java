package br.com.codexia.snippet.application.dto.command;

public record RestoreCategoryCommand(
        String categoryId,
        String workspaceId
) {}
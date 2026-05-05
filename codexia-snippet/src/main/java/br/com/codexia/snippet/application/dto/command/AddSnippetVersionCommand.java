package br.com.codexia.snippet.application.dto.command;

public record AddSnippetVersionCommand(
        String snippetId,
        String workspaceId,
        String title,
        String description,
        String content,
        String language
) {}

package br.com.codexia.snippet.application.dto.command;

public record DeleteSnippetCommand(
        String snippetId,
        String workspaceId
) {}

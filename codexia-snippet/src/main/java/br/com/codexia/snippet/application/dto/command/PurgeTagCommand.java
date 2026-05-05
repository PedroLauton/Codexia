package br.com.codexia.snippet.application.dto.command;

public record PurgeTagCommand(
        String tagId,
        String workspaceId
) {
}

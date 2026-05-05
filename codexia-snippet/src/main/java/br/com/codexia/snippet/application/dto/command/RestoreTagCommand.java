package br.com.codexia.snippet.application.dto.command;

public record RestoreTagCommand(
        String tagId,
        String workspaceId
) {
}

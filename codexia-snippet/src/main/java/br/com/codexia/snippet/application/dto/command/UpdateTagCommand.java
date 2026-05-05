package br.com.codexia.snippet.application.dto.command;

public record UpdateTagCommand(
        String tagId,
        String workspaceId,
        String title,
        String hexColor
) {
}

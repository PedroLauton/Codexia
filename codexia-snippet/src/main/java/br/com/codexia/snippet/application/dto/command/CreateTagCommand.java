package br.com.codexia.snippet.application.dto.command;

public record CreateTagCommand(
        String workspaceId,
        String title,
        String hexColor
) {
}

package br.com.codexia.snippet.application.dto.command;

public record DeleteTagCommand(
        String tagId,
        String workspaceId
) {
}

package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteSnippetCommand;
import br.com.codexia.snippet.application.ports.input.DeleteSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;

public class DeleteSnippetUseCaseImpl implements DeleteSnippetUseCase {

    private final SnippetCommandPort snippetCommandPort;
    private final SnippetQueryPort snippetQueryPort;

    public DeleteSnippetUseCaseImpl(SnippetCommandPort snippetCommandPort, SnippetQueryPort snippetQueryPort) {
        this.snippetCommandPort = snippetCommandPort;
        this.snippetQueryPort = snippetQueryPort;
    }

    @Override
    public void execute(DeleteSnippetCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        SnippetId snippetId = SnippetId.fromString(command.snippetId());

        Snippet snippet = findSnippetOrThrow(snippetId, workspaceId);

        snippet.delete();

        snippetCommandPort.save(snippet);
    }

    private Snippet findSnippetOrThrow(SnippetId snippetId, WorkspaceId workspaceId) {
        return snippetQueryPort.findById(snippetId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet with id: " + snippetId + " not found"));
    }
}

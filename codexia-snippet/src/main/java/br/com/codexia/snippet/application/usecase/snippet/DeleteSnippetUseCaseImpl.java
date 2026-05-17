package br.com.codexia.snippet.application.usecase.snippet;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteSnippetCommand;
import br.com.codexia.snippet.application.ports.input.snippet.DeleteSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.domain.model.aggregate.Snippet;
import br.com.codexia.snippet.domain.model.valueobject.SnippetId;

public class DeleteSnippetUseCaseImpl implements DeleteSnippetUseCase {

    private final SnippetCommandPort snippetCommandPort;
    private final SnippetFinder snippetFinder;

    public DeleteSnippetUseCaseImpl(SnippetCommandPort snippetCommandPort,
                                    SnippetFinder snippetFinder) {
        this.snippetCommandPort = snippetCommandPort;
        this.snippetFinder = snippetFinder;
    }

    @Override
    public void execute(DeleteSnippetCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        SnippetId snippetId = SnippetId.fromString(command.snippetId());

        Snippet snippet = snippetFinder.findActiveOrThrow(snippetId, workspaceId);

        snippet.delete();

        snippetCommandPort.save(snippet);
    }
}

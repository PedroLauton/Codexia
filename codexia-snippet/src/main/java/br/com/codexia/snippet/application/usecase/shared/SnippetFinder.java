package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;

public class SnippetFinder {

    private final SnippetQueryPort snippetQueryPort;

    public SnippetFinder(SnippetQueryPort snippetQueryPort) {
        this.snippetQueryPort = snippetQueryPort;
    }

    public Snippet findActiveOrThrow(SnippetId id, WorkspaceId workspaceId) {
        return snippetQueryPort.findById(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Snippet with id: " + id.value() + " not found"));
    }
}

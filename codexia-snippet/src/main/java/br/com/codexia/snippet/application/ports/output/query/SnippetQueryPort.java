package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;

import java.util.Optional;

public interface SnippetQueryPort {
    Optional<Snippet> findById(SnippetId id, WorkspaceId workspaceId);

}

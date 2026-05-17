package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
import br.com.codexia.snippet.domain.model.aggregate.Snippet;
import br.com.codexia.snippet.domain.model.valueobject.SnippetId;

import java.util.Optional;

public interface SnippetQueryPort {
    Optional<Snippet> findById(SnippetId id, WorkspaceId workspaceId);
    boolean existsActiveSnippetsByCategoryId(CategoryId categoryId, WorkspaceId workspaceId);
}

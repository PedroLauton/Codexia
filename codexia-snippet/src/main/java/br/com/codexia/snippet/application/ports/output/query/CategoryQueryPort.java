package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;

import java.util.Optional;

public interface CategoryQueryPort {
    Optional<Category> findById(CategoryId id, WorkspaceId workspaceId);
    Optional<Category> findDeletedById(CategoryId id, WorkspaceId workspaceId);
    boolean existsById(CategoryId id, WorkspaceId workspaceId);
    boolean existsByNameAndWorkspace(String name, WorkspaceId workspaceId);
    boolean existsByNameAndWorkspace(String name, WorkspaceId workspaceId, CategoryId excludeId);
}

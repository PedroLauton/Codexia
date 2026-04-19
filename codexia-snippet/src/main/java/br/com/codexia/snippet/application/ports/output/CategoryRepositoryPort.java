package br.com.codexia.snippet.application.ports.output;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.CategoryId;

public interface CategoryRepositoryPort {
    boolean existsById(CategoryId id, WorkspaceId workspaceId);
}

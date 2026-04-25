package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.CategoryId;

public interface CategoryQueryPort {
    boolean existsById(CategoryId id, WorkspaceId workspaceId);
}

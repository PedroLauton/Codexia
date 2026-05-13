package br.com.codexia.snippet.application.ports.output.command;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;

public interface CategoryCommandPort {
    void save(Category category);
    void delete(CategoryId categoryId);
    void updateSubtreeDepth(CategoryId rootId, int rootNewDepth, WorkspaceId workspaceId);
}

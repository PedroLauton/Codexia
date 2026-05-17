package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class CategoryFinder {

    private final CategoryQueryPort categoryQueryPort;

    public CategoryFinder(CategoryQueryPort categoryQueryPort) {
        this.categoryQueryPort = categoryQueryPort;
    }

    public Category findActiveOrThrow(CategoryId id, WorkspaceId workspaceId) {
        return categoryQueryPort.findById(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with id: " + id.value() + " not found"));
    }

    public Category findDeletedOrThrow(CategoryId id, WorkspaceId workspaceId) {
        return categoryQueryPort.findDeletedById(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deleted category with id: " + id.value() + " not found"));
    }
}

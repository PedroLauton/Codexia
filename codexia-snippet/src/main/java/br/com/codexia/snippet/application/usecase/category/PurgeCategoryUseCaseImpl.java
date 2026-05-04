package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.input.category.PurgeCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;

public class PurgeCategoryUseCaseImpl implements PurgeCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    public PurgeCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort, CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public void execute(DeleteCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        validateCategoryIsDeleted(categoryId, workspaceId);

        categoryCommandPort.delete(categoryId);
    }

    private void validateCategoryIsDeleted(CategoryId categoryId, WorkspaceId workspaceId) {
        Category category = categoryQueryPort.findDeletedById(categoryId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deleted category with id: " + categoryId.value() + " not found"));
    }
}

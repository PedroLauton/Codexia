package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.input.category.DeleteCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.exception.CategoryHasActiveSnippetsException;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;

public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;
    private final SnippetQueryPort snippetQueryPort;

    public DeleteCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,CategoryQueryPort categoryQueryPort, SnippetQueryPort snippetQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryQueryPort = categoryQueryPort;
        this.snippetQueryPort = snippetQueryPort;
    }

    @Override
    public void execute(DeleteCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = findCategoryOrThrow(categoryId, workspaceId);
        validateCategoryHasNoActiveSnippets(categoryId, workspaceId);

        category.delete();

        categoryCommandPort.save(category);
    }

    private Category findCategoryOrThrow(CategoryId categoryId, WorkspaceId workspaceId) {
        return categoryQueryPort.findById(categoryId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with id: " + categoryId.value() + " not found"));
    }

    private void validateCategoryHasNoActiveSnippets(CategoryId categoryId, WorkspaceId workspaceId) {
        if(snippetQueryPort.existsActiveSnippetsByCategoryId(categoryId, workspaceId)) {
            throw new CategoryHasActiveSnippetsException(categoryId);
        }
    }
}

package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.input.category.DeleteCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.CategoryHasActiveSnippetsException;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;
    private final SnippetQueryPort snippetQueryPort;

    public DeleteCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                     CategoryFinder categoryFinder,
                                     SnippetQueryPort snippetQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
        this.snippetQueryPort = snippetQueryPort;
    }

    @Override
    public void execute(DeleteCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = categoryFinder.findActiveOrThrow(categoryId, workspaceId);
        validateCategoryHasNoActiveSnippets(categoryId, workspaceId);

        category.delete();

        categoryCommandPort.save(category);
    }

    private void validateCategoryHasNoActiveSnippets(CategoryId categoryId, WorkspaceId workspaceId) {
        if (snippetQueryPort.existsActiveSnippetsByCategoryId(categoryId, workspaceId)) {
            throw new CategoryHasActiveSnippetsException(categoryId);
        }
    }
}

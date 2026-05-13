package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.input.category.PurgeCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.CategoryHasChildrenException;
import br.com.codexia.snippet.domain.model.CategoryId;

public class PurgeCategoryUseCaseImpl implements PurgeCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;
    private final CategoryQueryPort categoryQueryPort;

    public PurgeCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                    CategoryFinder categoryFinder,
                                    CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public void execute(DeleteCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        categoryFinder.findDeletedOrThrow(categoryId, workspaceId);

        if (categoryQueryPort.hasChildren(categoryId, workspaceId)) {
            throw new CategoryHasChildrenException(categoryId);
        }

        categoryCommandPort.delete(categoryId);
    }
}

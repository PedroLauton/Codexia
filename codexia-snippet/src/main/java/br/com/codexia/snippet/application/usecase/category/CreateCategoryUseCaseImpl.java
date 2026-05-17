package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.CreateCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.CategoryMaxDepthExceededException;
import br.com.codexia.snippet.domain.exception.category.DuplicateCategoryNameException;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;
    private final CategoryQueryPort categoryQueryPort;

    public CreateCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                     CategoryFinder categoryFinder,
                                     CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public CategoryResponse execute(CreateCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());

        CategoryId parentId = null;
        int depth = 0;

        if (command.parentId() != null) {
            CategoryId rawParentId = CategoryId.fromString(command.parentId());
            Category parent = categoryFinder.findActiveOrThrow(rawParentId, workspaceId);
            depth = parent.getDepth() + 1;
            if (depth > Category.MAX_DEPTH) {
                throw new CategoryMaxDepthExceededException(rawParentId, depth);
            }
            parentId = rawParentId;
        }

        validateNameUniqueness(command.name(), workspaceId);

        Category newCategory = new Category(workspaceId, command.name(), command.description(), parentId, depth);
        categoryCommandPort.save(newCategory);

        return CategoryResponseMapper.toResponse(newCategory);
    }

    private void validateNameUniqueness(String name, WorkspaceId workspaceId) {
        if (categoryQueryPort.existsByNameAndWorkspace(name, workspaceId)) {
            throw new DuplicateCategoryNameException(name, workspaceId);
        }
    }
}

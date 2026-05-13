package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.ReparentCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.ReparentCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.CategoryCircularReferenceException;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;

public class ReparentCategoryUseCaseImpl implements ReparentCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;
    private final CategoryQueryPort categoryQueryPort;

    public ReparentCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                       CategoryFinder categoryFinder,
                                       CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public CategoryResponse execute(ReparentCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = categoryFinder.findActiveOrThrow(categoryId, workspaceId);
    CategoryId newParentId = resolveParentId(command.parentId());
        int newDepth = resolveNewDepth(newParentId, categoryId, workspaceId);

        category.reparent(newParentId, newDepth);
        categoryCommandPort.save(category);
        categoryCommandPort.updateSubtreeDepth(categoryId, newDepth, workspaceId);

        return CategoryResponseMapper.toResponse(category);
    }

    private CategoryId resolveParentId(String rawParentId) {
        if (rawParentId == null) {
            return null;
        }
        return CategoryId.fromString(rawParentId);
    }

    private int resolveNewDepth(CategoryId newParentId, CategoryId categoryId, WorkspaceId workspaceId) {
        if (newParentId == null) {
            return 0;
        }
        validateNoCircularReference(categoryId, newParentId, workspaceId);
        Category parent = categoryFinder.findActiveOrThrow(newParentId, workspaceId);
        return parent.getDepth() + 1;
    }

    private void validateNoCircularReference(CategoryId categoryId, CategoryId newParentId, WorkspaceId workspaceId) {
        if (categoryQueryPort.isAncestorOf(categoryId, newParentId, workspaceId)) {
            throw new CategoryCircularReferenceException(categoryId, newParentId);
        }
    }
}

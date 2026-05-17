package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.UpdateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.UpdateCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.DuplicateCategoryNameException;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;
    private final CategoryQueryPort categoryQueryPort;

    public UpdateCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                     CategoryFinder categoryFinder,
                                     CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public CategoryResponse execute(UpdateCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = categoryFinder.findActiveOrThrow(categoryId, workspaceId);
        validateNameUniqueness(command.name(), workspaceId, categoryId);

        category.updateMetadata(command.name(), command.description());

        categoryCommandPort.save(category);

        return CategoryResponseMapper.toResponse(category);
    }

    private void validateNameUniqueness(String name, WorkspaceId workspaceId, CategoryId excludeId) {
        if (categoryQueryPort.existsByNameAndWorkspace(name, workspaceId, excludeId)) {
            throw new DuplicateCategoryNameException(name, workspaceId);
        }
    }
}

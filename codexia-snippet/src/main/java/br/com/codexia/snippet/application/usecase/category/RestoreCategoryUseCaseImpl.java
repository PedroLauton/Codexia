package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.RestoreCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.RestoreCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;

public class RestoreCategoryUseCaseImpl implements RestoreCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    public RestoreCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort, CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public CategoryResponse execute(RestoreCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = findDeletedCategoryOrThrow(categoryId, workspaceId);

        category.restore();

        categoryCommandPort.save(category);

        return CategoryResponseMapper.toResponse(category);
    }

    private Category findDeletedCategoryOrThrow(CategoryId categoryId, WorkspaceId workspaceId) {
        return categoryQueryPort.findDeletedById(categoryId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deleted category with id: " + categoryId.value() + " not found"));
    }
}

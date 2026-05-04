package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.CreateCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.domain.exception.DuplicateCategoryNameException;
import br.com.codexia.snippet.domain.model.Category;

public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    public CreateCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,CategoryQueryPort categoryQueryPort) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryQueryPort = categoryQueryPort;
    }

    @Override
    public CategoryResponse execute(CreateCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());

        validateNameUniqueness(command.name(), workspaceId);

        Category newCategory = new Category(workspaceId, command.name(), command.description());

        categoryCommandPort.save(newCategory);

        return CategoryResponseMapper.toResponse(newCategory);
    }

    private void validateNameUniqueness(String name, WorkspaceId workspaceId) {
        if (categoryQueryPort.existsByNameAndWorkspace(name, workspaceId)) {
            throw new DuplicateCategoryNameException(name, workspaceId);
        }
    }
}

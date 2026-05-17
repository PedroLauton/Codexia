package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.RestoreCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.RestoreCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.usecase.mapper.CategoryResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class RestoreCategoryUseCaseImpl implements RestoreCategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryFinder categoryFinder;

    public RestoreCategoryUseCaseImpl(CategoryCommandPort categoryCommandPort,
                                      CategoryFinder categoryFinder) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryFinder = categoryFinder;
    }

    @Override
    public CategoryResponse execute(RestoreCategoryCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());

        Category category = categoryFinder.findDeletedOrThrow(categoryId, workspaceId);

        category.restore();

        categoryCommandPort.save(category);

        return CategoryResponseMapper.toResponse(category);
    }
}

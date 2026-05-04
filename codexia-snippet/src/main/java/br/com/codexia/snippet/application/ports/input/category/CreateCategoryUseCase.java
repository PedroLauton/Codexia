package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.CreateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;

public interface CreateCategoryUseCase {
    CategoryResponse execute(CreateCategoryCommand command);
}

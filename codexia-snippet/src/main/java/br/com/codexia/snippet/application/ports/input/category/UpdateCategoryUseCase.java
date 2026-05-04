package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.UpdateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;

public interface UpdateCategoryUseCase {
    CategoryResponse execute(UpdateCategoryCommand command);
}
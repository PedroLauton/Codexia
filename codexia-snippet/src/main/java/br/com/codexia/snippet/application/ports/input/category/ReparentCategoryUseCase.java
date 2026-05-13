package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.ReparentCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;

public interface ReparentCategoryUseCase {
    CategoryResponse execute(ReparentCategoryCommand command);
}

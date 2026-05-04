package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.RestoreCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;

public interface RestoreCategoryUseCase {
    CategoryResponse execute(RestoreCategoryCommand command);
}



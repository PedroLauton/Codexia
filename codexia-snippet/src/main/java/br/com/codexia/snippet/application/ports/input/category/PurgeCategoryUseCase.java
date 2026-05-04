package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;

public interface PurgeCategoryUseCase {
    void execute(DeleteCategoryCommand command);
}

package br.com.codexia.snippet.application.ports.input.category;

import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;

public interface DeleteCategoryUseCase {
    void execute(DeleteCategoryCommand command);
}


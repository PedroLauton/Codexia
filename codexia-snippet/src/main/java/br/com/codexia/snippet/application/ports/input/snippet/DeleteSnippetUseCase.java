package br.com.codexia.snippet.application.ports.input.snippet;

import br.com.codexia.snippet.application.dto.command.DeleteSnippetCommand;

public interface DeleteSnippetUseCase {
    void execute(DeleteSnippetCommand command);
}


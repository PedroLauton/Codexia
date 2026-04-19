package br.com.codexia.snippet.application.ports.input;

import br.com.codexia.snippet.application.dto.command.CreateSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetResponse;

public interface CreateSnippetUseCase {
    SnippetResponse execute(CreateSnippetCommand command);
}

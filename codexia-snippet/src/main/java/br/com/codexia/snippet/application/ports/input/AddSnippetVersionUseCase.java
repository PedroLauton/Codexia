package br.com.codexia.snippet.application.ports.input;

import br.com.codexia.snippet.application.dto.command.AddSnippetVersionCommand;
import br.com.codexia.snippet.application.dto.response.SnippetVersionAddedResponse;

public interface AddSnippetVersionUseCase {
    SnippetVersionAddedResponse execute(AddSnippetVersionCommand command);
}

package br.com.codexia.snippet.application.ports.input.snippet;

import br.com.codexia.snippet.application.dto.command.ReassignSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetReassignedResponse;

public interface ReassignSnippetUseCase {
    SnippetReassignedResponse execute(ReassignSnippetCommand command);
}

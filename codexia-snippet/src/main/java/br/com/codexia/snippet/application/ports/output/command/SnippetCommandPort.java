package br.com.codexia.snippet.application.ports.output.command;

import br.com.codexia.snippet.domain.model.Snippet;

public interface SnippetCommandPort {
    void save(Snippet snippet);
}

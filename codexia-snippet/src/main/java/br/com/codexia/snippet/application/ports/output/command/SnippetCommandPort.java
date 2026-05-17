package br.com.codexia.snippet.application.ports.output.command;

import br.com.codexia.snippet.domain.model.aggregate.Snippet;

public interface SnippetCommandPort {
    void save(Snippet snippet);
}

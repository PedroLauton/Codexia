package br.com.codexia.snippet.application.ports.output;

import br.com.codexia.snippet.domain.model.Snippet;

public interface SnippetRepositoryPort {
    void save(Snippet snippet);
}

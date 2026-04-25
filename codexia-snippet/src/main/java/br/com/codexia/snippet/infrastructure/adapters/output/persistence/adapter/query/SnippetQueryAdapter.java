package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.SnippetJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.SnippetJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.CategoryJpaRepository;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.SnippetJpaRepository;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.TagJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SnippetQueryAdapter implements SnippetQueryPort {

    private final SnippetJpaRepository snippetJpaRepository;

    public SnippetQueryAdapter(SnippetJpaRepository snippetJpaRepository) {
        this.snippetJpaRepository = snippetJpaRepository;
    }

    @Override
    public Optional<Snippet> findById(SnippetId id, WorkspaceId workspaceId) {
        return Optional.empty();
    }
}

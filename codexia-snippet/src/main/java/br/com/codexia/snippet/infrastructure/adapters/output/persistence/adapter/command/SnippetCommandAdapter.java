package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.command;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
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
public class SnippetCommandAdapter implements SnippetCommandPort {

    private final SnippetJpaRepository snippetJpaRepository;

    // Fere o principio da segregação de interfaces ISP. Melhorar no decorrer do desenvolvimento, colocando
    // interfaces aqui, expondo apenas os metodos necessarios.
    private final CategoryJpaRepository categoryJpaRepository;
    private final TagJpaRepository tagJpaRepository;

    public SnippetCommandAdapter(SnippetJpaRepository snippetJpaRepository, CategoryJpaRepository categoryJpaRepository, TagJpaRepository tagJpaRepository) {
        this.snippetJpaRepository = snippetJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.tagJpaRepository = tagJpaRepository;

    }

    @Override
    public void save(Snippet snippet) {
        CategoryJpaEntity  category = categoryJpaRepository.getReferenceById(snippet.getCategoryId().value());

        Set<TagJpaEntity> tags = snippet.getTagIds().stream()
                .map(tagId -> tagJpaRepository.getReferenceById(tagId.value()))
                .collect(Collectors.toSet());

        SnippetJpaEntity snippetJpaEntity = SnippetJpaMapper.toEntity(snippet, category, tags);
        snippetJpaRepository.save(snippetJpaEntity);
    }
}

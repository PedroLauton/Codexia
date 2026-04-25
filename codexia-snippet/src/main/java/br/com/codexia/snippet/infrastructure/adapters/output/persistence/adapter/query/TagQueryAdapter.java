package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.TagJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.TagJpaRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagQueryAdapter implements TagQueryPort {

    private final TagJpaRepository tagJpaRepository;

    public TagQueryAdapter(TagJpaRepository tagJpaRepository) {
        this.tagJpaRepository = tagJpaRepository;
    }

    @Override
    public List<Tag> findAllByIds(Set<TagId> ids, WorkspaceId workspaceId) {
        List<TagJpaEntity> entityTags = tagJpaRepository.findAllByIdInAndWorkspaceId(
                ids.stream()
                        .map(TagId::value)
                        .collect(Collectors.toSet()),
                workspaceId.value());

        return entityTags.stream()
                .map(TagJpaMapper::toDomain)
                .toList();
    }
}

package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.TagJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.TagJpaRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagQueryAdapter implements TagQueryPort {

    private final TagJpaRepository tagJpaRepository;

    public TagQueryAdapter(TagJpaRepository tagJpaRepository) {
        this.tagJpaRepository = tagJpaRepository;
    }

    @Override
    public Optional<Tag> findById(TagId id, WorkspaceId workspaceId) {
        return tagJpaRepository.findByIdAndWorkspaceId(id.value(), workspaceId.value())
                .map(TagJpaMapper::toDomain);
    }

    @Override
    public Optional<Tag> findDeletedById(TagId id, WorkspaceId workspaceId) {
        return tagJpaRepository.findDeletedByIdAndWorkspaceId(id.value(), workspaceId.value())
                .map(TagJpaMapper::toDomain);
    }

    @Override
    public boolean existsByTitleAndWorkspace(String title, WorkspaceId workspaceId) {
        return tagJpaRepository.existsByTitleAndWorkspaceId(title, workspaceId.value());
    }

    @Override
    public boolean existsByTitleAndWorkspace(String title, WorkspaceId workspaceId, TagId excludeId) {
        return tagJpaRepository.existsByTitleAndWorkspaceIdAndIdNot(title, workspaceId.value(), excludeId.value());
    }

    @Override
    public List<Tag> findAllByIds(Set<TagId> ids, WorkspaceId workspaceId) {
        return tagJpaRepository.findAllByIdInAndWorkspaceId(
                        ids.stream().map(TagId::value).collect(Collectors.toSet()),
                        workspaceId.value())
                .stream()
                .map(TagJpaMapper::toDomain)
                .toList();
    }
}

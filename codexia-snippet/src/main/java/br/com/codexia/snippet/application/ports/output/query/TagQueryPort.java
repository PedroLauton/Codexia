package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagQueryPort {
    Optional<Tag> findById(TagId id, WorkspaceId workspaceId);
    Optional<Tag> findDeletedById(TagId id, WorkspaceId workspaceId);
    boolean existsByTitleAndWorkspace(String title, WorkspaceId workspaceId);
    boolean existsByTitleAndWorkspace(String title, WorkspaceId workspaceId, TagId excludeId);
    List<Tag> findAllByIds(Set<TagId> ids, WorkspaceId workspaceId);
}
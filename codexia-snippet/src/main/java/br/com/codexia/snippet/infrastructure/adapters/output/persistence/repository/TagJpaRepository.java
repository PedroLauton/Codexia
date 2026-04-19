package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.TagId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, UUID> {
    List<TagJpaEntity> findAllByIdInAndWorkspaceId(Set<UUID> ids, UUID workspaceId);
}

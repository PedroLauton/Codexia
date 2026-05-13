package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, UUID> {

    Optional<TagJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<TagJpaEntity> findAllByIdInAndWorkspaceId(Set<UUID> ids, UUID workspaceId);

    boolean existsByTitleAndWorkspaceId(String title, UUID workspaceId);

    boolean existsByTitleAndWorkspaceIdAndIdNot(String title, UUID workspaceId, UUID excludeId);

    @Query(value = "SELECT * FROM tags WHERE id = :id AND workspace_id = :workspaceId AND deleted_at IS NOT NULL",
            nativeQuery = true)
    Optional<TagJpaEntity> findDeletedByIdAndWorkspaceId(@Param("id") UUID id,
                                                          @Param("workspaceId") UUID workspaceId);
}

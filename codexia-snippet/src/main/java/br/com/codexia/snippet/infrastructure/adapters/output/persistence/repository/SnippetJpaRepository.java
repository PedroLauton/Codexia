package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.SnippetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SnippetJpaRepository extends JpaRepository<SnippetJpaEntity, UUID> {

    Optional<SnippetJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query(value = "SELECT * FROM snippets WHERE id = :id AND workspace_id = :workspaceId AND deleted_at IS NOT NULL",
            nativeQuery = true)
    Optional<SnippetJpaEntity> findDeletedByIdAndWorkspaceId(@Param("id") UUID id,
                                                              @Param("workspaceId") UUID workspaceId);

    boolean existsByCategory_IdAndWorkspaceId(UUID categoryId, UUID workspaceId);
}

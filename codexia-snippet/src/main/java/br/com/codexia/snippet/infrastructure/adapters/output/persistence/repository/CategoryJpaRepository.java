package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    boolean existsByIdAndWorkspaceId(UUID id, UUID workspaceId);

    Optional<CategoryJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByNameAndWorkspaceId(String name, UUID workspaceId);

    boolean existsByNameAndWorkspaceIdAndIdNot(String name, UUID workspaceId, UUID excludeId);

    @Query(value = "SELECT * FROM categories WHERE id = :id AND workspace_id = :workspaceId AND deleted_at IS NOT NULL",
            nativeQuery = true)
    Optional<CategoryJpaEntity> findDeletedByIdAndWorkspaceId(@Param("id") UUID id,
                                                               @Param("workspaceId") UUID workspaceId);

    @Query(value = """
            WITH RECURSIVE ancestors AS (
                SELECT id, parent_id FROM categories WHERE id = :targetId AND workspace_id = :workspaceId
                UNION ALL
                SELECT c.id, c.parent_id FROM categories c
                INNER JOIN ancestors a ON c.id = a.parent_id WHERE c.workspace_id = :workspaceId
            )
            SELECT COUNT(*) > 0 FROM ancestors WHERE id = :potentialAncestorId
            """, nativeQuery = true)
    boolean isAncestorOf(@Param("potentialAncestorId") UUID potentialAncestorId,
                         @Param("targetId") UUID targetId,
                         @Param("workspaceId") UUID workspaceId);

    @Query(value = "SELECT COUNT(*) > 0 FROM categories WHERE parent_id = :parentId AND workspace_id = :workspaceId",
            nativeQuery = true)
    boolean existsChildrenByParentIdAndWorkspaceId(@Param("parentId") UUID parentId,
                                                    @Param("workspaceId") UUID workspaceId);

    @Modifying
    @Query(value = """
            WITH RECURSIVE subtree AS (
                SELECT id, :rootNewDepth AS new_depth FROM categories
                    WHERE id = :rootId AND workspace_id = :workspaceId
                UNION ALL
                SELECT c.id, s.new_depth + 1 FROM categories c
                INNER JOIN subtree s ON c.parent_id = s.id WHERE c.workspace_id = :workspaceId
            )
            UPDATE categories SET depth = s.new_depth FROM subtree s WHERE categories.id = s.id
            """, nativeQuery = true)
    void updateSubtreeDepth(@Param("rootId") UUID rootId,
                            @Param("rootNewDepth") int rootNewDepth,
                            @Param("workspaceId") UUID workspaceId);
}

package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    boolean existsByIdAndWorkspaceId(UUID id, UUID workspaceId);
}

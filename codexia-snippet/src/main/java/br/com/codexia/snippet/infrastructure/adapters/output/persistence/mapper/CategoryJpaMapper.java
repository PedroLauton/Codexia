package br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;

// infrastructure/adapters/output/persistence/mapper/CategoryJpaMapper.java
public final class CategoryJpaMapper {

    private CategoryJpaMapper() {}

    public static CategoryJpaEntity toEntity(Category category) {
        return CategoryJpaEntity.builder()
                .id(category.getId().value())
                .workspaceId(category.getWorkspaceId().value())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }

    public static Category toDomain(CategoryJpaEntity entity) {
        return new Category(
                CategoryId.fromString(entity.getId().toString()),
                WorkspaceId.fromString(entity.getWorkspaceId().toString()),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

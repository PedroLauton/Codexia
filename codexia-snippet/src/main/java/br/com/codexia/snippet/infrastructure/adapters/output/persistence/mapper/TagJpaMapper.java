package br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;

// infrastructure/adapters/output/persistence/mapper/TagJpaMapper.java
public final class TagJpaMapper {

    private TagJpaMapper() {}

    public static TagJpaEntity toEntity(Tag tag) {
        return TagJpaEntity.builder()
                .id(tag.getId().value())
                .workspaceId(tag.getWorkspaceId().value())
                .title(tag.getTitle())
                .hexColor(tag.getHexColor())
                .createdAt(tag.getCreatedAt())
                .deletedAt(tag.getDeletedAt())
                .build();
    }

    public static Tag toDomain(TagJpaEntity entity) {
        return new Tag(
                TagId.fromString(entity.getId().toString()),
                WorkspaceId.fromString(entity.getWorkspaceId().toString()),
                entity.getTitle(),
                entity.getHexColor(),
                entity.getCreatedAt(),
                entity.getDeletedAt()
        );
    }
}
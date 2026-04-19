package br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper;

import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.*;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.SnippetJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.SnippetVersionJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.TagJpaEntity;

import java.util.Set;
import java.util.stream.Collectors;

public final class SnippetJpaMapper {

    private SnippetJpaMapper() {}

    public static SnippetJpaEntity toEntity(Snippet snippet, CategoryJpaEntity category, Set<TagJpaEntity> tags) {
        SnippetJpaEntity entity = SnippetJpaEntity.builder()
                .id(snippet.getId().value())
                .workspaceId(snippet.getWorkspaceId().value())
                .accountId(snippet.getAccountId().value())
                .category(category)
                .tags(tags)
                .createdAt(snippet.getCreatedAt())
                .updatedAt(snippet.getUpdatedAt())
                .deletedAt(snippet.getDeletedAt())
                .build();


                entity.setVersions(
                        snippet.getVersions().stream().map(x -> toVersionEntity(x, entity)).toList()
                );

                return entity;
    }

    public static Snippet toDomain(SnippetJpaEntity entity) {
        Set<TagId> tagIds = entity.getTags().stream()
                .map(tag -> TagId.fromString(tag.getId().toString()))
                .collect(Collectors.toUnmodifiableSet());

        Set<SnippetVersion> versions = entity.getVersions().stream()
                .map(SnippetJpaMapper::toVersionDomain)
                .collect(Collectors.toUnmodifiableSet());

        return new Snippet(
                SnippetId.fromString(entity.getId().toString()),
                WorkspaceId.fromString(entity.getWorkspaceId().toString()),
                AccountId.fromString(entity.getAccountId().toString()),
                CategoryId.fromString(entity.getCategory().getId().toString()),
                tagIds,
                versions,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    private static SnippetVersionJpaEntity toVersionEntity (SnippetVersion snippetVersion, SnippetJpaEntity snippetEntity) {
        return SnippetVersionJpaEntity.builder()
                .id(snippetVersion.getId().value())
                .snippet(snippetEntity)
                .title(snippetVersion.getTitle())
                .description(snippetVersion.getDescription())
                .content(snippetVersion.getContent())
                .language(snippetVersion.getLanguage())
                .createdAt(snippetVersion.getCreatedAt())
                .build();
    }

    private static SnippetVersion toVersionDomain(SnippetVersionJpaEntity entity) {
        return new SnippetVersion(
                SnippetVersionId.fromString(entity.getId().toString()),
                SnippetId.fromString(entity.getSnippet().getId().toString()),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent(),
                entity.getLanguage(),
                entity.getCreatedAt()
        );
    }
}

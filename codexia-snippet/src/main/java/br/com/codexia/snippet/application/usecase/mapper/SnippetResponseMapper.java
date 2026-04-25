package br.com.codexia.snippet.application.usecase.mapper;

import br.com.codexia.snippet.application.dto.response.*;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetVersion;
import br.com.codexia.snippet.domain.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

public final class SnippetResponseMapper {

    private SnippetResponseMapper() {}

    public static SnippetResponse toResponse(Snippet snippet, List<Tag> tags) {
        return new SnippetResponse(
                snippet.getId().value().toString(),
                snippet.getWorkspaceId().value().toString(),
                snippet.getAccountId().value().toString(),
                snippet.getCategoryId().value().toString(),

                tags.stream()
                        .map(SnippetResponseMapper::toTagSummary)
                        .collect(Collectors.toUnmodifiableSet()),

                snippet.getVersions().stream()
                        .map(SnippetResponseMapper::toVersionResponse)
                        .toList(),

                snippet.getCreatedAt(),
                snippet.getUpdatedAt()
            );

    }

    public static SnippetVersionAddedResponse toVersionAddedResponse(Snippet snippet, SnippetVersion version) {
        return new SnippetVersionAddedResponse(
                snippet.getId().value().toString(),
                toVersionResponse(version),
                snippet.getUpdatedAt()
        );
    }

    public static SnippetReassignedResponse toReassignedResponse(Snippet snippet,  List<Tag> tags) {
        return new SnippetReassignedResponse(
                snippet.getId().value().toString(),
                snippet.getWorkspaceId().value().toString(),
                snippet.getCategoryId().value().toString(),
                tags.stream()
                        .map(SnippetResponseMapper::toTagSummary)
                        .collect(Collectors.toUnmodifiableSet()),
                snippet.getUpdatedAt()
        );
    }

    private static TagSummaryResponse toTagSummary(Tag tag) {
        return new TagSummaryResponse(
                tag.getId().value().toString(),
                tag.getTitle(),
                tag.getHexColor()
        );
    }

    private static SnippetVersionResponse toVersionResponse(SnippetVersion version) {
        return new SnippetVersionResponse(
                version.getId().value().toString(),
                version.getTitle(),
                version.getDescription(),
                version.getContent(),
                version.getLanguage().name(),
                version.getCreatedAt()
        );
    }
}

package br.com.codexia.snippet.application.usecase.mapper;

import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.domain.model.aggregate.Tag;

public final class TagResponseMapper {
    private TagResponseMapper() {}

    public static TagResponse toResponse(Tag tag) {
        return new TagResponse(
                tag.getId().value().toString(),
                tag.getWorkspaceId().value().toString(),
                tag.getTitle(),
                tag.getHexColor(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }
}

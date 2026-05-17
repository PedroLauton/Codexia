package br.com.codexia.snippet.application.usecase.mapper;

import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.domain.model.aggregate.Category;

public final class CategoryResponseMapper {

    private CategoryResponseMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId().value().toString(),
                category.getWorkspaceId().value().toString(),
                category.getParentId() != null ? category.getParentId().value().toString() : null,
                category.getDepth(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}

package br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "name is mandatory")
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        String parentId
) {
}

package br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateSnippetRequest(
        @NotBlank(message = "title is mandatory")
        @Size(min = 3, max = 50, message = "The title must be between 3 and 50 characters")
        String title,

        @Size(max = 500, message = "The description must have a maximum of 500 characters")
        String description,

        @NotBlank(message = "content is mandatory")
        String content,

        @NotBlank(message = "language is mandatory")
        String language,

        @NotBlank(message = "categoryId is mandatory")
        String categoryId,

        @Size(max = 10, message = "Cannot exceed 10 tags.")
        Set<String> tagIds
) {
}

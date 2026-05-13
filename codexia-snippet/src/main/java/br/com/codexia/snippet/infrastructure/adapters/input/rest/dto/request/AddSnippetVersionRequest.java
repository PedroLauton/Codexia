package br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddSnippetVersionRequest(
        @NotBlank(message = "title is mandatory")
        @Size(min = 3, max = 50)
        String title,

        @Size(max = 500)
        String description,

        @NotBlank(message = "content is mandatory")
        String content,

        @NotBlank(message = "language is mandatory")
        String language
) {
}

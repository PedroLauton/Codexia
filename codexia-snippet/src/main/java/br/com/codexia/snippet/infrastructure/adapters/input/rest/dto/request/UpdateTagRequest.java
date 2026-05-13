package br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateTagRequest(
        @NotBlank(message = "title is mandatory")
        @Size(max = 50)
        String title,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid color format. Use Hexadecimal (ex: #FF0000).")
        String hexColor
) {
}

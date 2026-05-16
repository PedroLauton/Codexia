package br.com.codexia.shared.infrastructure.adapters.input.rest.dto;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        List<FieldErrorResponse> errors
) {}

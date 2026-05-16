package br.com.codexia.shared.infrastructure.adapters.input.rest.dto;

public record FieldErrorResponse(
        String field,
        String message
) {}

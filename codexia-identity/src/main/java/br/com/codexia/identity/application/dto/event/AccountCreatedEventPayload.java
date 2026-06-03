package br.com.codexia.identity.application.dto.event;

public record AccountCreatedEventPayload(
        String accountId,
        String email,
        String name
) {}
package br.com.codexia.identity.application.dto.event;

public record AccountDeletedEventPayload(
        String accountId
) {}
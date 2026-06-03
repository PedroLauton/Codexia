package br.com.codexia.identity.application.dto.command;

public record RegisterAccountCommand(
        String email,
        String name,
        String rawPassword
) {}
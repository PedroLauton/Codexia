package br.com.codexia.identity.application.dto.command;

public record RestoreAccountCommand(String accountId) {
    public RestoreAccountCommand {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("AccountId is mandatory.");
    }
}

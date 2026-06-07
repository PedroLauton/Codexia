package br.com.codexia.identity.application.dto.command;

public record DeleteAccountCommand(String accountId) {
    public DeleteAccountCommand {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("AccountId is mandatory.");
    }
}

package br.com.codexia.identity.application.dto.command;

public record ChangeEmailCommand(
        String accountId,
        String newEmail
) {
    public ChangeEmailCommand {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (newEmail == null || newEmail.isBlank())
            throw new IllegalArgumentException("New email is mandatory.");
    }
}

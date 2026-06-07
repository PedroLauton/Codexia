package br.com.codexia.identity.application.dto.command;

public record UpdateProfileCommand(
        String accountId,
        String name
) {
    public UpdateProfileCommand {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is mandatory.");
    }
}

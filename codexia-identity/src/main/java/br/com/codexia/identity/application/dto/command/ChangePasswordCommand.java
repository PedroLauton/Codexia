package br.com.codexia.identity.application.dto.command;

public record ChangePasswordCommand(
        String accountId,
        String currentPassword,
        String newPassword
) {
    public ChangePasswordCommand {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("AccountId is mandatory.");
        if (currentPassword == null || currentPassword.isBlank())
            throw new IllegalArgumentException("Current password is mandatory.");
        if (newPassword == null || newPassword.isBlank())
            throw new IllegalArgumentException("New password is mandatory.");
    }
}

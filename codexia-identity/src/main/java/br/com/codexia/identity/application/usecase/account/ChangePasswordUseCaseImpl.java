package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ChangePasswordCommand;
import br.com.codexia.identity.application.ports.input.ChangePasswordUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.service.PasswordEncoderPort;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.domain.exception.account.InvalidCredentialsException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Password;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.List;

public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountFinder accountFinder;
    private final PasswordEncoderPort passwordEncoderPort;

    public ChangePasswordUseCaseImpl(AccountCommandPort accountCommandPort, AccountFinder accountFinder, PasswordEncoderPort passwordEncoderPort) {
        this.accountCommandPort = accountCommandPort;
        this.accountFinder = accountFinder;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());
        Password currentPassword = Password.of(command.currentPassword());
        Password newPassword = Password.of(command.newPassword());

        Account account = accountFinder.findActiveOrThrow(accountId);

        PasswordHash currentHash = account.passwordHashForVerification();
        if (!passwordEncoderPort.matches(currentPassword, currentHash)) {
            throw new InvalidCredentialsException();
        }

        PasswordHash newPasswordHash = passwordEncoderPort.encode(newPassword);
        account.changePassword(newPasswordHash);

        accountCommandPort.save(account, List.of());
    }
}

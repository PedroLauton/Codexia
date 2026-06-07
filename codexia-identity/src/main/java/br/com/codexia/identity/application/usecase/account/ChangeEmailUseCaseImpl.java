package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ChangeEmailCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.ChangeEmailUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.List;

public class ChangeEmailUseCaseImpl implements ChangeEmailUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountFinder accountFinder;
    private final AccountValidationService accountValidationService;

    public ChangeEmailUseCaseImpl(AccountCommandPort accountCommandPort, AccountFinder accountFinder, AccountValidationService accountValidationService) {
        this.accountCommandPort = accountCommandPort;
        this.accountFinder = accountFinder;
        this.accountValidationService = accountValidationService;
    }

    @Override
    public AccountResponse execute(ChangeEmailCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());
        Email newEmail = Email.of(command.newEmail());

        Account account = accountFinder.findActiveOrThrow(accountId);
        accountValidationService.validateEmailAvailability(newEmail);

        account.changeEmail(newEmail);
        accountCommandPort.save(account, List.of());

        return AccountResponseMapper.toResponse(account);
    }
}

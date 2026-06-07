package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.UpdateProfileCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.UpdateProfileUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.List;

public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountFinder accountFinder;

    public UpdateProfileUseCaseImpl(AccountCommandPort accountCommandPort, AccountFinder accountFinder) {
        this.accountCommandPort = accountCommandPort;
        this.accountFinder = accountFinder;
    }

    @Override
    public AccountResponse execute(UpdateProfileCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());
        Name name = Name.of(command.name());

        Account account = accountFinder.findActiveOrThrow(accountId);
        account.updateProfile(name, account.getAvatarUrl());

        accountCommandPort.save(account, List.of());

        return AccountResponseMapper.toResponse(account);
    }
}

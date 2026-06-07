package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.RestoreAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.RestoreAccountUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.application.usecase.mapper.DomainEventOutboxMapper;
import br.com.codexia.identity.domain.exception.account.AccountNotFoundException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.List;

public class RestoreAccountUseCaseImpl implements RestoreAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountQueryPort accountQueryPort;

    public RestoreAccountUseCaseImpl(AccountCommandPort accountCommandPort, AccountQueryPort accountQueryPort) {
        this.accountCommandPort = accountCommandPort;
        this.accountQueryPort = accountQueryPort;
    }

    @Override
    public AccountResponse execute(RestoreAccountCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());

        Account account = findDeletedOrThrow(accountId);
        account.restore();

        List<OutboxEvent> outboxEvents = DomainEventOutboxMapper.toOutboxEvents(account.pullEvents());
        accountCommandPort.save(account, outboxEvents);

        return AccountResponseMapper.toResponse(account);
    }

    private Account findDeletedOrThrow(AccountId accountId) {
        return accountQueryPort.findDeletedById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}

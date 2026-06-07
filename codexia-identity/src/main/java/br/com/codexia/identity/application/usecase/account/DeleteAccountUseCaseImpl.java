package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.DeleteAccountCommand;
import br.com.codexia.identity.application.ports.input.DeleteAccountUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.usecase.mapper.DomainEventOutboxMapper;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.List;

public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountFinder accountFinder;

    public DeleteAccountUseCaseImpl(AccountCommandPort accountCommandPort, AccountFinder accountFinder) {
        this.accountCommandPort = accountCommandPort;
        this.accountFinder = accountFinder;
    }

    @Override
    public void execute(DeleteAccountCommand command) {
        AccountId accountId = AccountId.fromString(command.accountId());
        Account account = accountFinder.findActiveOrThrow(accountId);

        account.delete();

        List<OutboxEvent> outboxEvents = DomainEventOutboxMapper.toOutboxEvents(account.pullEvents());
        accountCommandPort.save(account, outboxEvents);
    }
}

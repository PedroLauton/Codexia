package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ResolveOAuthAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.ResolveOAuthAccountUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.application.usecase.mapper.DomainEventOutboxMapper;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.AvatarUrl;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.ProviderName;
import br.com.codexia.shared.application.outbox.OutboxEvent;

import java.util.List;

public class ResolveOAuthAccountUseCaseImpl implements ResolveOAuthAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountQueryPort accountQueryPort;
    private final AccountValidationService accountValidationService;

    public ResolveOAuthAccountUseCaseImpl(AccountCommandPort accountCommandPort, AccountQueryPort accountQueryPort, AccountValidationService accountValidationService) {
        this.accountCommandPort = accountCommandPort;
        this.accountQueryPort = accountQueryPort;
        this.accountValidationService = accountValidationService;
    }

    @Override
    public AccountResponse execute(ResolveOAuthAccountCommand command) {
        Email email = Email.of(command.email());
        ProviderName providerName = ProviderName.of(command.providerName());

        Account account = accountQueryPort.findByEmail(email)
                .map(existing -> prepareExistingAccount(existing, providerName, command.providerId()))
                .orElseGet(() -> buildNewOAuthAccount(command, email, providerName));

        List<OutboxEvent> outboxEvents = DomainEventOutboxMapper.toOutboxEvents(account.pullEvents());
        accountCommandPort.save(account, outboxEvents);

        return AccountResponseMapper.toResponse(account);
    }

    private Account prepareExistingAccount(Account account, ProviderName providerName, String providerId) {
        accountValidationService.validateAccountNotDeleted(account);

        if (!account.hasExternalIdentities(providerName)) {
            account.linkOAuthCredential(providerName, providerId);
        }

        return account;
    }

    private Account buildNewOAuthAccount(ResolveOAuthAccountCommand command, Email email, ProviderName providerName) {
        Name name = Name.of(command.name());
        AvatarUrl avatarUrl = AvatarUrl.of(command.avatarUrl());

        return Account.createWithOAuth(email, name, avatarUrl, providerName, command.providerId());
    }
}

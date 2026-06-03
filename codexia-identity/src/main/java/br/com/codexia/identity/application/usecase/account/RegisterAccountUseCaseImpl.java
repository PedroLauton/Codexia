package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.RegisterAccountCommand;
import br.com.codexia.identity.application.dto.event.AccountCreatedEventPayload;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.RegisterAccountUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.application.ports.output.service.PasswordEncoderPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.domain.event.AccountCreatedEvent;
import br.com.codexia.identity.domain.exception.account.AccountAlreadyExistsException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.Password;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.event.DomainEvent;

import java.util.List;

public class RegisterAccountUseCaseImpl implements RegisterAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountQueryPort accountQueryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterAccountUseCaseImpl(AccountCommandPort accountCommandPort, AccountQueryPort accountQueryPort, PasswordEncoderPort passwordEncoderPort) {
        this.accountCommandPort = accountCommandPort;
        this.accountQueryPort = accountQueryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public AccountResponse execute(RegisterAccountCommand command) {
        Email email = Email.of(command.email());
        Name name = Name.of(command.name());
        Password password = Password.of(command.rawPassword());

        validateEmailAvailability(email);

        String passwordHash  = passwordEncoderPort.encode(password.value());
        Account account = Account.createWithCredentials(email, name, passwordHash);

        List<OutboxEvent> outboxEvents = buildOutboxEvents(account);
        accountCommandPort.save(account, outboxEvents);

        return AccountResponseMapper.toResponse(account);
    }

    private void validateEmailAvailability(Email email) {
        accountQueryPort.findByEmail(email)
                .ifPresent(existing -> {
                    throw new AccountAlreadyExistsException(email.value());
                });
    }

    private List<OutboxEvent> buildOutboxEvents(Account account) {
        return account.pullEvents().stream()
                .map(this::toOutboxEvent)
                .toList();
    }

    private OutboxEvent toOutboxEvent(DomainEvent event) {
        if (event instanceof AccountCreatedEvent e) {
            return OutboxEvent.of(
                    e.accountId().value(),
                    "account",
                    "AccountCreatedEvent",
                    new AccountCreatedEventPayload(
                            e.accountId().value().toString(),
                            e.email().value(),
                            e.name().value()
                    )
            );
        }
        throw new UnsupportedOperationException(
                "No outbox mapping for event: " + event.getClass().getName());
    }
}

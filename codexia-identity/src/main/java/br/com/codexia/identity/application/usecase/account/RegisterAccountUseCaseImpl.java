package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.RegisterAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.input.RegisterAccountUseCase;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.service.PasswordEncoderPort;
import br.com.codexia.identity.application.usecase.mapper.AccountResponseMapper;
import br.com.codexia.identity.application.usecase.mapper.DomainEventOutboxMapper;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.Password;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.application.outbox.OutboxEvent;

import java.util.List;

public class RegisterAccountUseCaseImpl implements RegisterAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountValidationService accountValidationService;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterAccountUseCaseImpl(AccountCommandPort accountCommandPort, AccountValidationService accountValidationService, PasswordEncoderPort passwordEncoderPort) {
        this.accountCommandPort = accountCommandPort;
        this.accountValidationService = accountValidationService;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public AccountResponse execute(RegisterAccountCommand command) {
        Email email = Email.of(command.email());
        Name name = Name.of(command.name());
        Password password = Password.of(command.rawPassword());

        accountValidationService.validateEmailAvailability(email);

        PasswordHash passwordHash = passwordEncoderPort.encode(password);
        Account account = Account.createWithCredentials(email, name, passwordHash);

        List<OutboxEvent> outboxEvents = DomainEventOutboxMapper.toOutboxEvents(account.pullEvents());
        accountCommandPort.save(account, outboxEvents);

        return AccountResponseMapper.toResponse(account);
    }
}

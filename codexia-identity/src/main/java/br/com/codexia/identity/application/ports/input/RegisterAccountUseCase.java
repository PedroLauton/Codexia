package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.RegisterAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;

public interface RegisterAccountUseCase {
    AccountResponse execute(RegisterAccountCommand command);
}

package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.RestoreAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;

public interface RestoreAccountUseCase {
    AccountResponse execute(RestoreAccountCommand command);
}
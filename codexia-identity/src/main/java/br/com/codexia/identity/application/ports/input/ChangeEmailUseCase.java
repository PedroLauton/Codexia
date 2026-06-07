package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.ChangeEmailCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;

public interface ChangeEmailUseCase {
    AccountResponse execute(ChangeEmailCommand command);
}
package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.UpdateProfileCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;

public interface UpdateProfileUseCase {
    AccountResponse execute(UpdateProfileCommand command);
}
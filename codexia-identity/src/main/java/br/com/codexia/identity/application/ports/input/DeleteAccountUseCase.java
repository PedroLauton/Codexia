package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.DeleteAccountCommand;

public interface DeleteAccountUseCase {
    void execute(DeleteAccountCommand command);
}

package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void execute(ChangePasswordCommand command);
}
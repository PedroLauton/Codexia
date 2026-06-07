package br.com.codexia.identity.application.ports.input;

import br.com.codexia.identity.application.dto.command.ResolveOAuthAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;

public interface ResolveOAuthAccountUseCase {
    AccountResponse execute(ResolveOAuthAccountCommand command);
}

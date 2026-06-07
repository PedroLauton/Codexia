package br.com.codexia.identity.application.usecase.shared;

import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.domain.exception.account.AccountAlreadyExistsException;
import br.com.codexia.identity.domain.exception.account.AccountDeletedException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;

public class AccountValidationService {

    private final AccountQueryPort accountQueryPort;

    public AccountValidationService(AccountQueryPort accountQueryPort) {
        this.accountQueryPort = accountQueryPort;
    }

    public void validateEmailAvailability(Email email) {
        accountQueryPort.findByEmail(email)
                .ifPresent(existing -> {
                    throw new AccountAlreadyExistsException(email.value());
                });
    }

    public void validateAccountNotDeleted(Account account) {
        if (account.isDeleted()) {
            throw new AccountDeletedException(account.getId());
        }
    }
}

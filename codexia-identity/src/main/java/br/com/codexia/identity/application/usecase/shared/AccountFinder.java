package br.com.codexia.identity.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;

public class AccountFinder {

    private final AccountQueryPort accountQueryPort;

    public AccountFinder(AccountQueryPort accountQueryPort) {
        this.accountQueryPort = accountQueryPort;
    }

    public Account findActiveOrThrow(AccountId id) {
        return accountQueryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account with id [" + id.value() + "] not found."));
    }

    public Account findByEmailOrThrow(Email email) {
        return accountQueryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account with email [" + email.value() + "] not found."));
    }
}
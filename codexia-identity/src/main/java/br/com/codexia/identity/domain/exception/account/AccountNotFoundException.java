package br.com.codexia.identity.domain.exception.account;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.AccountId;

public class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(AccountId id) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                "Account with id [" + id.value() + "] not found.");
    }

    public AccountNotFoundException(String email) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                "Account with email [" + email + "] not found.");
    }
}

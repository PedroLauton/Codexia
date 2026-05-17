package br.com.codexia.identity.domain.exception.account;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;

public class AccountAlreadyExistsException extends DomainException {
    public AccountAlreadyExistsException(String email) {
        super(ErrorCode.ACCOUNT_ALREADY_EXISTS,
                "Account with email [" + email + "] already exists.");
    }
}
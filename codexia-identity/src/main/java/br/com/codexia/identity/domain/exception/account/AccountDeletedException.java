package br.com.codexia.identity.domain.exception.account;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.AccountId;

public class AccountDeletedException extends DomainException {
    public AccountDeletedException(AccountId id) {
        super(ErrorCode.ACCOUNT_DELETED,
                "Account [" + id.value() + "] is deleted.");
    }
}
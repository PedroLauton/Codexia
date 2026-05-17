package br.com.codexia.identity.domain.exception.account;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.AccountId;

public class AccountNotDeletedException extends DomainException {
    public AccountNotDeletedException(AccountId id) {
        super(ErrorCode.ACCOUNT_NOT_DELETED,
                "Account [" + id.value() + "] is not deleted and cannot be restored.");
    }
}
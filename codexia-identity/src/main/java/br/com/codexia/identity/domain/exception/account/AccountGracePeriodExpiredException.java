package br.com.codexia.identity.domain.exception.account;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.AccountId;

public class AccountGracePeriodExpiredException extends DomainException {
    public AccountGracePeriodExpiredException(AccountId id) {
        super(ErrorCode.ACCOUNT_GRACE_PERIOD_EXPIRED,
                "Grace period for account [" + id.value() + "] has expired.");
    }
}

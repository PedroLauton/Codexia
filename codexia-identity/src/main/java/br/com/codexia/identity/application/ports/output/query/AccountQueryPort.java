package br.com.codexia.identity.application.ports.output.query;

import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.shared.domain.model.AccountId;

import java.util.Optional;

public interface AccountQueryPort {
    Optional<Account> findByEmail(Email email);
    Optional<Account> findById(AccountId id);
}
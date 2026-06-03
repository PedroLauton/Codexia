package br.com.codexia.identity.application.usecase.mapper;

import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.domain.model.aggregate.Account;

public final class AccountResponseMapper {

    private AccountResponseMapper() {}

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId().value().toString(),
                account.getEmail().value(),
                account.getName().value(),
                account.getAvatarUrl() != null ? account.getAvatarUrl().value() : null,
                account.getRole().name(),
                account.getCreatedAt()
        );
    }
}

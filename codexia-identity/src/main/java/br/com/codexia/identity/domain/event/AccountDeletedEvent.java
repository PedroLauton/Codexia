package br.com.codexia.identity.domain.event;

import br.com.codexia.shared.domain.event.DomainEvent;
import br.com.codexia.shared.domain.model.AccountId;

public record AccountDeletedEvent(
        AccountId accountId
) implements DomainEvent {}
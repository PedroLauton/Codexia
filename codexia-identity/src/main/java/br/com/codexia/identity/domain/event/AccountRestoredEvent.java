package br.com.codexia.identity.domain.event;

import br.com.codexia.shared.domain.event.DomainEvent;
import br.com.codexia.shared.domain.model.AccountId;

public record AccountRestoredEvent(
        AccountId accountId
) implements DomainEvent {}

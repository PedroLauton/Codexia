package br.com.codexia.identity.domain.event;

import br.com.codexia.shared.domain.event.DomainEvent;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.shared.domain.model.AccountId;

public record AccountCreatedEvent(
        AccountId accountId,
        Email email,
        Name name
) implements DomainEvent {}
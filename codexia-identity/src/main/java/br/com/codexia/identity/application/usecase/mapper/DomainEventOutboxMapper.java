package br.com.codexia.identity.application.usecase.mapper;

import br.com.codexia.identity.application.dto.event.AccountCreatedEventPayload;
import br.com.codexia.identity.application.dto.event.AccountDeletedEventPayload;
import br.com.codexia.identity.application.dto.event.AccountRestoredEventPayload;
import br.com.codexia.identity.domain.event.AccountCreatedEvent;
import br.com.codexia.identity.domain.event.AccountDeletedEvent;
import br.com.codexia.identity.domain.event.AccountRestoredEvent;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.event.DomainEvent;

import java.util.List;

public final class DomainEventOutboxMapper {

    private DomainEventOutboxMapper() {}

    public static List<OutboxEvent> toOutboxEvents(List<DomainEvent> domainEvents) {
        return domainEvents.stream()
                .map(DomainEventOutboxMapper::toOutboxEvent)
                .toList();
    }

    private static OutboxEvent toOutboxEvent(DomainEvent event) {
        if (event instanceof AccountCreatedEvent e) {
            return OutboxEvent.of(
                    e.accountId().value(),
                    "account",
                    "AccountCreatedEvent",
                    new AccountCreatedEventPayload(
                            e.accountId().value().toString(),
                            e.email().value(),
                            e.name().value()
                    )
            );
        }
        if (event instanceof AccountDeletedEvent e) {
            return OutboxEvent.of(
                    e.accountId().value(),
                    "account",
                    "AccountDeletedEvent",
                    new AccountDeletedEventPayload(
                            e.accountId().value().toString()
                    )
            );
        }
        if (event instanceof AccountRestoredEvent e) {
            return OutboxEvent.of(
                    e.accountId().value(),
                    "account",
                    "AccountRestoredEvent",
                    new AccountRestoredEventPayload(
                            e.accountId().value().toString()
                    )
            );
        }
        throw new UnsupportedOperationException(
                "No outbox mapping for event: " + event.getClass().getName());
    }
}

package br.com.codexia.identity.application.ports.output.command;

import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.shared.application.outbox.OutboxEvent;

import java.util.List;

public interface AccountCommandPort {
    void save(Account account, List<OutboxEvent> outboxEvents);
}

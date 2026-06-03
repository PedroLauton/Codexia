package br.com.codexia.shared.application.ports.output;

import br.com.codexia.shared.domain.model.OutboxEvent;

public interface OutboxEventPort {
    void save(OutboxEvent event);
}
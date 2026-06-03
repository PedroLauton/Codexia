package br.com.codexia.shared.application.outbox;

import java.time.Instant;
import java.util.UUID;

public class OutboxEvent {

    private final UUID id;
    private final UUID aggregateId;
    private final String aggregateType;
    private final String eventType;
    private final Object payload;
    private final Instant createdAt;

    private OutboxEvent(UUID aggregateId, String aggregateType,
                        String eventType, Object payload) {
        if (aggregateId == null)
            throw new IllegalArgumentException("AggregateId is mandatory.");
        if (aggregateType == null || aggregateType.isBlank())
            throw new IllegalArgumentException("AggregateType is mandatory.");
        if (eventType == null || eventType.isBlank())
            throw new IllegalArgumentException("EventType is mandatory.");
        if (payload == null)
            throw new IllegalArgumentException("Payload is mandatory.");

        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public static OutboxEvent of(UUID aggregateId, String aggregateType,
                                 String eventType, Object payload) {
        return new OutboxEvent(aggregateId, aggregateType, eventType, payload);
    }

    public UUID getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public String getEventType() { return eventType; }
    public Object getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
}
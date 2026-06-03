CREATE TABLE outbox_events (
                               id             UUID         NOT NULL,
                               aggregate_id   UUID         NOT NULL,
                               aggregate_type VARCHAR(100) NOT NULL,
                               event_type     VARCHAR(100) NOT NULL,
                               payload        JSONB        NOT NULL,
                               created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

                               CONSTRAINT pk_outbox_events PRIMARY KEY (id)
);
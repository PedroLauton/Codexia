CREATE TABLE tags (
    id           UUID         NOT NULL,
    workspace_id UUID         NOT NULL,
    title        VARCHAR(100) NOT NULL,
    hex_color    VARCHAR(7),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at   TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_tags PRIMARY KEY (id)
);

CREATE INDEX idx_tags_workspace_id ON tags (workspace_id);
CREATE UNIQUE INDEX idx_tags_title_active
    ON tags (workspace_id, title)
    WHERE deleted_at IS NULL;
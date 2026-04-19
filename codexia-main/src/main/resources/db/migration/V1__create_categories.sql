CREATE TABLE categories (
    id          UUID        NOT NULL,
    workspace_id UUID       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at  TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE INDEX idx_categories_workspace_id ON categories (workspace_id);
CREATE UNIQUE INDEX idx_categories_name_active
    ON categories (workspace_id, name)
    WHERE deleted_at IS NULL;
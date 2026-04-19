CREATE TABLE snippets (
    id           UUID NOT NULL,
    workspace_id UUID NOT NULL,
    account_id   UUID NOT NULL,
    category_id  UUID NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at   TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_snippets PRIMARY KEY (id),
    CONSTRAINT fk_snippets_category
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_snippets_workspace_id ON snippets (workspace_id);
CREATE INDEX idx_snippets_category_id  ON snippets (category_id);
CREATE INDEX idx_snippets_account_id   ON snippets (account_id);
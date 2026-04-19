CREATE TABLE snippet_versions (
    id         UUID         NOT NULL,
    snippet_id UUID         NOT NULL,
    title      VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    content    TEXT         NOT NULL,
    language   VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_snippet_versions PRIMARY KEY (id),
    CONSTRAINT fk_snippet_versions_snippet
    FOREIGN KEY (snippet_id) REFERENCES snippets (id)
);

CREATE INDEX idx_snippet_versions_snippet_id ON snippet_versions (snippet_id);
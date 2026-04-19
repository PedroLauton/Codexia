CREATE TABLE snippet_tags (
    snippet_id UUID NOT NULL,
    tag_id     UUID NOT NULL,

    CONSTRAINT pk_snippet_tags PRIMARY KEY (snippet_id, tag_id),
    CONSTRAINT fk_snippet_tags_snippet
    FOREIGN KEY (snippet_id) REFERENCES snippets (id),
    CONSTRAINT fk_snippet_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags (id)
);
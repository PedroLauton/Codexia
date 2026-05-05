ALTER TABLE snippet_versions
DROP CONSTRAINT fk_snippet_versions_snippet,
    ADD CONSTRAINT fk_snippet_versions_snippet
        FOREIGN KEY (snippet_id) REFERENCES snippets (id)
            ON DELETE CASCADE;

ALTER TABLE snippet_tags
DROP CONSTRAINT fk_snippet_tags_snippet,
    ADD CONSTRAINT fk_snippet_tags_snippet
        FOREIGN KEY (snippet_id) REFERENCES snippets (id)
            ON DELETE CASCADE;
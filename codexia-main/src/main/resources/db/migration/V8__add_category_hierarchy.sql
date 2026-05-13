ALTER TABLE categories
    ADD COLUMN parent_id UUID,
    ADD COLUMN depth     INT NOT NULL DEFAULT 0;

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id)
        REFERENCES categories (id)
        DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE categories
    ADD CONSTRAINT chk_categories_depth
        CHECK (depth >= 0 AND depth <= 5);

ALTER TABLE categories
    ADD CONSTRAINT chk_categories_no_self_ref
        CHECK (parent_id <> id);

CREATE INDEX idx_categories_parent_id ON categories (parent_id)
    WHERE parent_id IS NOT NULL;

package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.WorkspaceId;

import java.time.Instant;

public class Category {

    private final CategoryId id;
    private WorkspaceId workspaceId;
    private String name;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Category(WorkspaceId workspaceId, String name, String description) {

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is mandatory.");
        if (workspaceId == null) throw new IllegalArgumentException("Workspace is mandatory.");

        this.id = CategoryId.generate();
        this.workspaceId = workspaceId;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateMetadata(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name and description cannot be null.");
        }

        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("Category already deleted");
        }

        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

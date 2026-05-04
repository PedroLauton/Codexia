package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.exception.CategoryNotDeletedException;
import br.com.codexia.snippet.domain.exception.DeletedCategoryMutationException;

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

    public Category(CategoryId id, WorkspaceId workspaceId, String name, String description, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public void updateMetadata(String name, String description) {
        checkNotDeleted();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is mandatory.");
        }

        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void restore() {
        if (!isDeleted()) {
            throw new CategoryNotDeletedException(this.id);
        }

        this.deletedAt = null;
        this.updatedAt = Instant.now();
    }


    public void delete() {
        checkNotDeleted();
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public CategoryId getId() {
        return id;
    }

    public WorkspaceId getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    private void checkNotDeleted() {
        if (isDeleted()) {
            throw new DeletedCategoryMutationException(this.id);
        }
    }
}

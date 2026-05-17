package br.com.codexia.snippet.domain.model.aggregate;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.exception.category.CategoryMaxDepthExceededException;
import br.com.codexia.snippet.domain.exception.category.CategoryNotDeletedException;
import br.com.codexia.snippet.domain.exception.category.CategorySelfReferenceException;
import br.com.codexia.snippet.domain.exception.category.DeletedCategoryMutationException;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

import java.time.Instant;

public class Category {

    public static final int MAX_DEPTH = 5;

    private final CategoryId id;
    private WorkspaceId workspaceId;
    private CategoryId parentId;
    private int depth;
    private String name;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Category(WorkspaceId workspaceId, String name, String description, CategoryId parentId, int depth) {

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is mandatory.");
        if (workspaceId == null) throw new IllegalArgumentException("Workspace is mandatory.");

        this.id = CategoryId.generate();
        this.workspaceId = workspaceId;
        this.parentId = parentId;
        this.depth = depth;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Category(CategoryId id, WorkspaceId workspaceId, String name, String description,
                    CategoryId parentId, int depth,
                    Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.parentId = parentId;
        this.depth = depth;
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

    public void reparent(CategoryId newParentId, int newDepth) {
        checkNotDeleted();
        if (newParentId != null && newParentId.equals(this.id)) {
            throw new CategorySelfReferenceException(this.id);
        }
        if (newDepth > MAX_DEPTH) {
            throw new CategoryMaxDepthExceededException(this.id, newDepth);
        }
        this.parentId = newParentId;
        this.depth = newDepth;
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

    public CategoryId getParentId() {
        return parentId;
    }

    public int getDepth() {
        return depth;
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

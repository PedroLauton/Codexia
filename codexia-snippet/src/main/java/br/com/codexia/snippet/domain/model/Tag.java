package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.exception.DeletedTagMutationException;

import java.time.Instant;

public class Tag {

    private final TagId id;
    private final WorkspaceId workspaceId;
    private String title;
    private String hexColor;
    private final Instant createdAt;
    private Instant deletedAt;


    public Tag(WorkspaceId workspaceId, String title, String hexColor) {
        if (workspaceId == null) throw new IllegalArgumentException("WorkspaceId is mandatory.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is mandatory.");
        if (hexColor != null && !hexColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid color format. Use Hexadecimal (ex: #FF0000).");
        }

        this.id = TagId.generate();
        this.workspaceId = workspaceId;
        this.title = title;
        this.hexColor = hexColor;
        this.createdAt = Instant.now();
    }

    public Tag(TagId id, WorkspaceId workspaceId, String title, String hexColor, Instant createdAt, Instant deletedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.title = title;
        this.hexColor = hexColor;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public TagId getId() {
        return id;
    }

    public WorkspaceId getWorkspaceId() {
        return workspaceId;
    }

    public String getTitle() {
        return title;
    }

    public String getHexColor() {
        return hexColor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void delete() {
        if (this.deletedAt != null)
            throw new DeletedTagMutationException("Operation rejected: Tag [" + this.id + "] is deleted.");
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

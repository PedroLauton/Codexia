package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Snippet {

    //TODO: Alterar regra e local
    private static final int MAX_TAGS = 10;

    private final SnippetId id;
    private final WorkspaceId workspaceId;
    private final AccountId accountId;
    private CategoryId categoryId;
    private Set<TagId> tagIds;
    private Set<SnippetVersion> versions = new HashSet<>();
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Snippet(WorkspaceId workspaceId, AccountId accountId, CategoryId categoryId, Set<TagId> tagIds, String title, String description, String content, Language language) {

        if (workspaceId == null) throw new IllegalArgumentException("Workspace is mandatory.");
        if (accountId == null) throw new IllegalArgumentException("Account is mandatory.");
        if (categoryId == null) throw new IllegalArgumentException("Category is mandatory.");

        this.tagIds = tagIds != null ? new HashSet<>(tagIds) : new HashSet<>();

        if (this.tagIds.size() > MAX_TAGS) {
            throw new IllegalArgumentException("The snippet cannot exceed the limit of " + MAX_TAGS + " tags.");
        }

        this.id = SnippetId.generate();
        this.workspaceId = workspaceId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.tagIds = tagIds;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.addVersion(title, description, content, language);
    }

    public void addVersion(String title, String description, String content, Language language) {
        this.versions.add(new SnippetVersion(this.id, title, description, content, language));
        this.updatedAt = Instant.now();
    }

    public void assignToCategory(CategoryId categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        this.categoryId = categoryId;
    }

    public void linkTag(TagId tagId) {
        if (tagId == null) {
            throw new IllegalArgumentException("The tag cannot be null.");
        }

        if (this.tagIds.size() >= MAX_TAGS) {
            throw new IllegalStateException("Maximum limit of  " + MAX_TAGS + " tags reached.");
        }

        this.tagIds.add(tagId);
        this.updatedAt = Instant.now();
    }

    public void unlinkTag(TagId tagId) {
        if (tagId == null) return;

        boolean removed = this.tagIds.remove(tagId);
        
        if (removed) {
            this.updatedAt = Instant.now();
        }
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("Snippet already deleted");
        }

        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}

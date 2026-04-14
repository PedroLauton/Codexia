package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.exception.DeletedSnippetMutationException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Snippet {

    // TODO: Mover para SnippetPolicy ou SnippetDomainService quando
    //  a regra exigir variação por Workspace ou plano de assinatura (quem sabe?).
    private static final int MAX_TAGS = 10;

    private final SnippetId id;
    private final WorkspaceId workspaceId;
    private final AccountId accountId;
    private CategoryId categoryId;
    private Set<TagId> tagIds;
    private Set<SnippetVersion> versions;
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
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.versions = new HashSet<>();

        this.versions.add(buildVersion(title, description, content, language));
    }

    public void addVersion(String title, String description, String content, Language language) {
        checkNotDeleted();
        this.versions.add(buildVersion(title, description, content, language));
        this.updatedAt = Instant.now();
    }

    public void assignToCategory(CategoryId categoryId) {
        checkNotDeleted();
        if (categoryId == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        this.categoryId = categoryId;
    }

    public void linkTag(TagId tagId) {
        checkNotDeleted();
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
        checkNotDeleted();
        if (tagId == null) return;

        boolean removed = this.tagIds.remove(tagId);

        if (removed) {
            this.updatedAt = Instant.now();
        }
    }

    public void delete() {
        checkNotDeleted();
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    private void checkNotDeleted() {
        if (this.deletedAt != null) {
            throw new DeletedSnippetMutationException(this.id);
        }
    }

    private SnippetVersion buildVersion(String title, String description, String content, Language language) {
        return new SnippetVersion(this.id, title, description, content, language);
    }
}

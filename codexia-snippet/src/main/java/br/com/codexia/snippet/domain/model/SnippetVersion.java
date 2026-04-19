package br.com.codexia.snippet.domain.model;

import java.time.Instant;

public class SnippetVersion {

    private final SnippetVersionId id;
    private final SnippetId snippetId;
    private final String title;
    private final String description;
    private final String content;
    private final Language language;
    private final Instant createdAt;

    public SnippetVersion(SnippetId snippetId, String title, String description, String content, Language language) {

        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is mandatory.");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Content is mandatory.");
        if (language == null) throw new IllegalArgumentException("Language is mandatory.");

        this.id = SnippetVersionId.generate();
        this.snippetId = snippetId;
        this.title = title;
        this.description = description;
        this.content = content;
        this.language = language;
        this.createdAt = Instant.now();
    }

    public SnippetVersion(SnippetVersionId id, SnippetId snippetId, String title,  String description, String content, Language language,  Instant createdAt) {
        this.id = id;
        this.snippetId = snippetId;
        this.title = title;
        this.description = description;
        this.content = content;
        this.language = language;
        this.createdAt = createdAt;
    }

    public SnippetVersionId getId() {
        return id;
    }

    public SnippetId getSnippetId() {
        return snippetId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public Language getLanguage() {
        return language;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

package br.com.codexia.snippet.domain.model;

import java.util.UUID;

public record SnippetVersionId(UUID value) {
    public SnippetVersionId {
        if (value == null) {
            throw new IllegalArgumentException("SnippetVersionId cannot be null");
        }
    }

    public static SnippetVersionId generate() {
        return new SnippetVersionId(UUID.randomUUID());
    }

    public static SnippetVersionId fromString(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("SnippetVersionId cannot be null or blank");
        }
        return new SnippetVersionId(UUID.fromString(uuid));
    }
}
package br.com.codexia.snippet.domain.model;

import java.util.UUID;

public record SnippetId(UUID value) {
    public SnippetId {
        if (value == null) {
            throw new IllegalArgumentException("SnippetId cannot be null");
        }
    }

    public static SnippetId generate() {
        return new SnippetId(UUID.randomUUID());
    }

    public static SnippetId fromString(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("SnippetId cannot be null or blank");
        }
        return new SnippetId(UUID.fromString(uuid));
    }
}
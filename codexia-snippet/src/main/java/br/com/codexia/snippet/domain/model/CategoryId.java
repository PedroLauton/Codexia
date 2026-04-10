package br.com.codexia.snippet.domain.model;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId fromString(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("CategoryId cannot be null or blank");
        }
        return new CategoryId(UUID.fromString(uuid));
    }
}
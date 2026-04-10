package br.com.codexia.snippet.domain.model;

import java.util.UUID;

public record TagId(UUID value) {
    public TagId {
        if (value == null) {
            throw new IllegalArgumentException("TagId cannot be null");
        }
    }

    public static TagId generate() {
        return new TagId(UUID.randomUUID());
    }

    public static TagId fromString(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("TagId cannot be null or blank");
        }
        return new TagId(UUID.fromString(uuid));
    }
}
package br.com.codexia.identity.domain.model.valueobject;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Password hash is mandatory.");
    }

    public static PasswordHash of(String value) {
        return new PasswordHash(value);
    }
}

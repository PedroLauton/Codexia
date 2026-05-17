package br.com.codexia.identity.domain.model.valueobject;

public record Name(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 20;

    public Name {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Name cannot be blank.");

        value = value.trim();

        if (value.length() < MIN_LENGTH)
            throw new IllegalArgumentException(
                    "Name must be at least " + MIN_LENGTH + " characters.");
        if (value.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                    "Name must be at most " + MAX_LENGTH + " characters.");
    }

    public static Name of(String value) {
        return new Name(value);
    }
}

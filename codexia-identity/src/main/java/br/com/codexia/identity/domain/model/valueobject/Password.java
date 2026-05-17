package br.com.codexia.identity.domain.model.valueobject;

import java.util.regex.Pattern;

public record Password(String value) {

    public static final int MIN_LENGTH = 8;

    private static final Pattern HAS_NUMBER =
            Pattern.compile(".*\\d.*");

    private static final Pattern HAS_SPECIAL =
            Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?].*");

    public Password {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Password cannot be blank.");
        if (value.length() < MIN_LENGTH)
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_LENGTH + " characters.");
        if (!HAS_NUMBER.matcher(value).matches())
            throw new IllegalArgumentException(
                    "Password must contain at least one number.");
        if (!HAS_SPECIAL.matcher(value).matches())
            throw new IllegalArgumentException(
                    "Password must contain at least one special character.");
    }

    public static Password of(String rawPassword) {
        return new Password(rawPassword);
    }

    @Override
    public String toString() {
        return "Password[PROTECTED]";
    }
}

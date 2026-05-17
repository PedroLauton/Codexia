package br.com.codexia.identity.domain.model.valueobject;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern FORMAT =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Email cannot be blank.");

        value = value.trim().toLowerCase();

        if (!FORMAT.matcher(value).matches())
            throw new IllegalArgumentException("Invalid email format.");
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
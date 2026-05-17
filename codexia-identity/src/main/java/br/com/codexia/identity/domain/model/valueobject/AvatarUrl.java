package br.com.codexia.identity.domain.model.valueobject;

import java.util.regex.Pattern;

public record AvatarUrl(String value) {

    private static final Pattern URL_FORMAT =
            Pattern.compile("^https?://.*");

    public AvatarUrl {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("AvatarUrl cannot be blank.");

        value = value.trim();

        if (!URL_FORMAT.matcher(value).matches())
            throw new IllegalArgumentException("Invalid URL format.");
    }

    public static AvatarUrl of(String value) {
        return new AvatarUrl(value);
    }
}
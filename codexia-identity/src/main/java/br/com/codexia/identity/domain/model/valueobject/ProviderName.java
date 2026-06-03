package br.com.codexia.identity.domain.model.valueobject;

import java.util.Locale;

public record ProviderName(String value) {
    public ProviderName {
        if (value == null) {
            throw new IllegalArgumentException("Provider is mandatory.");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Provider name cannot be blank.");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
    }

    public static ProviderName of(String value) {
        return new ProviderName(value);
    }
}
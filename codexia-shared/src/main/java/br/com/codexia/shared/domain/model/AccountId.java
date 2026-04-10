package br.com.codexia.shared.domain.model;

import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        if(value==null){
            throw new IllegalArgumentException("AccountId cannot be null");
        }
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId fromString(String uuid) {
        if(uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("AccountId cannot be null or blank");
        }
        return new AccountId(UUID.fromString(uuid));
    }
}

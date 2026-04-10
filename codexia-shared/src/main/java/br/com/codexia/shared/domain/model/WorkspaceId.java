package br.com.codexia.shared.domain.model;

import java.util.UUID;

public record WorkspaceId(UUID value) {

    public WorkspaceId {
        if(value==null){
            throw new IllegalArgumentException("WorkspaceId cannot be null");
        }
    }

    public static WorkspaceId generate() {
        return new WorkspaceId(UUID.randomUUID());
    }

    public static WorkspaceId fromString(String uuid) {
        if(uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("WorkspaceId cannot be null or blank");
        }
        return new WorkspaceId(UUID.fromString(uuid));
    }
}
package br.com.codexia.identity.domain.model.enums;

import java.util.Collections;
import java.util.Set;

public enum AccountRole {

    USER(Set.of()),

    ADMIN(Set.of(
            Permission.ACCOUNT_LIST_ALL,
            Permission.ACCOUNT_PROMOTE,
            Permission.ACCOUNT_PURGE,
            Permission.SYSTEM_ADMIN,
            Permission.SNIPPET_MODERATE,
            Permission.WORKSPACE_MODERATE
    ));

    private final Set<Permission> permissions;

    AccountRole(Set<Permission> permissions) {
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    public Set<Permission> getPermissions() { return permissions; }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
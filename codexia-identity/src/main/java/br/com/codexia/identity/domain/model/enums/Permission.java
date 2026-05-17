package br.com.codexia.identity.domain.model.enums;

public enum Permission {

    // permissões de sistema — SYSTEM_ADMIN
    ACCOUNT_LIST_ALL,
    ACCOUNT_PROMOTE,
    ACCOUNT_PURGE,
    SYSTEM_ADMIN,

    // permissões de moderação — acesso irrestrito a dados
    SNIPPET_MODERATE,    // editar/deletar qualquer snippet
    WORKSPACE_MODERATE;  // acessar qualquer workspace
}

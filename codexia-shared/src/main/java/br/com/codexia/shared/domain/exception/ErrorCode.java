package br.com.codexia.shared.domain.exception;

public enum ErrorCode {

    // Transversal
    RESOURCE_NOT_FOUND("RN001", "Resource not found."),
    UNAUTHORIZED("RN002", "Unauthorized."),
    INVALID_ARGUMENT("RN003", "Invalid argument."),

    // Snippet — prefixo SN
    DELETED_SNIPPET_MUTATION("SN001", "Operation rejected: snippet is deleted."),
    SNIPPET_TAG_LIMIT_EXCEEDED("SN002", "Maximum tag limit reached."),
    SNIPPET_VERSION_IMMUTABLE("SN003", "Snippet version cannot be mutated."),

    // Tag
    DELETED_TAG_MUTATION("TG001", "Operation rejected: tag is deleted."),
    DUPLICATE_TAG_TITLE("TG002", "Tag title already exists in this workspace."),
    TAG_NOT_DELETED("TG003", "Tag is not deleted and cannot be restored."),

    // Category
    CATEGORY_HAS_ACTIVE_SNIPPETS("CT001", "Category has active snippets."),
    DELETED_CATEGORY_MUTATION("CT001", "Operation rejected: category is deleted."),
    DUPLICATE_CATEGORY_NAME("CT002", "Category name already exists in this workspace."),
    CATEGORY_NOT_DELETED("CT003", "Category is not deleted and cannot be restored."),

    // Workspace — prefixo WS
    WORKSPACE_MEMBER_ALREADY_EXISTS("WS001", "Member already exists in workspace."),
    WORKSPACE_OWNER_CANNOT_BE_REMOVED("WS002", "Workspace owner cannot be removed."),

    // Identity — prefixo ID
    ACCOUNT_ALREADY_EXISTS("ID001", "Account already exists."),
    INVALID_CREDENTIALS("ID002", "Invalid credentials."),

    // AI — prefixo AI
    AI_PROVIDER_UNAVAILABLE("AI001", "AI provider unavailable."),
    AI_TASK_FAILED("AI002", "AI task failed."),

    // Notification — prefixo NT
    NOTIFICATION_DELIVERY_FAILED("NT001", "Notification delivery failed.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() { return code; }
    public String getDefaultMessage() { return defaultMessage; }
}

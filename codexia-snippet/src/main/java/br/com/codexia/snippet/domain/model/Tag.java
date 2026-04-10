package br.com.codexia.snippet.domain.model;

import br.com.codexia.shared.domain.model.WorkspaceId;

public class Tag {

    private final TagId id;
    private final WorkspaceId workspaceId;
    private String title;
    private String hexColor;

    public Tag(WorkspaceId workspaceId, String title, String hexColor) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is mandatory.");
        if (hexColor != null && !hexColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid color format. Use Hexadecimal (ex: #FF0000).");
        }

        this.id = TagId.generate();
        this.workspaceId = workspaceId;
        this.title = title;
        this.hexColor = hexColor;
    }
}

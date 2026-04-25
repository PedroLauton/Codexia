package br.com.codexia.snippet.application.dto.command;

import java.util.Collections;
import java.util.Set;

public record ReassignSnippetCommand(
        String snippetId,
        String workspaceId,
        String categoryId,
        Set<String> tagIds
) {
    public ReassignSnippetCommand {
        tagIds = (tagIds == null) ? Collections.emptySet() : Set.copyOf(tagIds);
    }
}

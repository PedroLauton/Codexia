package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;

public class TagFinder {

    private final TagQueryPort tagQueryPort;

    public TagFinder(TagQueryPort tagQueryPort) {
        this.tagQueryPort = tagQueryPort;
    }

    public Tag findActiveOrThrow(TagId id, WorkspaceId workspaceId) {
        return tagQueryPort.findById(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag with id: " + id.value() + " not found"));
    }

    public Tag findDeletedOrThrow(TagId id, WorkspaceId workspaceId) {
        return tagQueryPort.findDeletedById(id, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deleted tag with id: " + id.value() + " not found"));
    }
}

package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.PurgeTagCommand;
import br.com.codexia.snippet.application.ports.input.tag.PurgeTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.TagId;

public class PurgeTagUseCaseImpl implements PurgeTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    public PurgeTagUseCaseImpl(TagCommandPort tagCommandPort, TagQueryPort tagQueryPort) {
        this.tagCommandPort = tagCommandPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public void execute(PurgeTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        validateTagIsDeleted(tagId, workspaceId);

        tagCommandPort.delete(tagId);
    }

    private void validateTagIsDeleted(TagId tagId, WorkspaceId workspaceId) {
        tagQueryPort.findDeletedById(tagId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Deleted tag with id: " + tagId.value() + " not found"));
    }
}

package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteTagCommand;
import br.com.codexia.snippet.application.ports.input.tag.DeleteTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;

public class DeleteTagUseCaseImpl implements DeleteTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    public DeleteTagUseCaseImpl(TagCommandPort tagCommandPort, TagQueryPort tagQueryPort) {
        this.tagCommandPort = tagCommandPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public void execute(DeleteTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        Tag tag = findTagOrThrow(tagId, workspaceId);

        tag.delete();

        tagCommandPort.save(tag);
    }

    private Tag findTagOrThrow(TagId tagId, WorkspaceId workspaceId) {
        return tagQueryPort.findById(tagId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag with id: " + tagId.value() + " not found"));
    }
}

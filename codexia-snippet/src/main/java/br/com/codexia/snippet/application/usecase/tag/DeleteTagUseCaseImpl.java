package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteTagCommand;
import br.com.codexia.snippet.application.ports.input.tag.DeleteTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;

public class DeleteTagUseCaseImpl implements DeleteTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagFinder tagFinder;

    public DeleteTagUseCaseImpl(TagCommandPort tagCommandPort, TagFinder tagFinder) {
        this.tagCommandPort = tagCommandPort;
        this.tagFinder = tagFinder;
    }

    @Override
    public void execute(DeleteTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        Tag tag = tagFinder.findActiveOrThrow(tagId, workspaceId);

        tag.delete();

        tagCommandPort.save(tag);
    }
}

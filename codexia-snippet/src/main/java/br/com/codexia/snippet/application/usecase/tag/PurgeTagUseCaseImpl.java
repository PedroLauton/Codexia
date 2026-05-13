package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.PurgeTagCommand;
import br.com.codexia.snippet.application.ports.input.tag.PurgeTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.domain.model.TagId;

public class PurgeTagUseCaseImpl implements PurgeTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagFinder tagFinder;

    public PurgeTagUseCaseImpl(TagCommandPort tagCommandPort, TagFinder tagFinder) {
        this.tagCommandPort = tagCommandPort;
        this.tagFinder = tagFinder;
    }

    @Override
    public void execute(PurgeTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        tagFinder.findDeletedOrThrow(tagId, workspaceId);

        tagCommandPort.delete(tagId);
    }
}

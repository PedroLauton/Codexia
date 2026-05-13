package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.RestoreTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.input.tag.RestoreTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.usecase.mapper.TagResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;

public class RestoreTagUseCaseImpl implements RestoreTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagFinder tagFinder;

    public RestoreTagUseCaseImpl(TagCommandPort tagCommandPort, TagFinder tagFinder) {
        this.tagCommandPort = tagCommandPort;
        this.tagFinder = tagFinder;
    }

    @Override
    public TagResponse execute(RestoreTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        Tag tag = tagFinder.findDeletedOrThrow(tagId, workspaceId);

        tag.restore();
        tagCommandPort.save(tag);

        return TagResponseMapper.toResponse(tag);
    }
}

package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.input.tag.CreateTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.TagResponseMapper;
import br.com.codexia.snippet.domain.exception.tag.DuplicateTagTitleException;
import br.com.codexia.snippet.domain.model.aggregate.Tag;

import java.util.Locale;

public class CreateTagUseCaseImpl implements CreateTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    public CreateTagUseCaseImpl(TagCommandPort tagCommandPort, TagQueryPort tagQueryPort) {
        this.tagCommandPort = tagCommandPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public TagResponse execute(CreateTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());

        validateTitleUniqueness(command.title(), workspaceId);

        Tag newTag = new Tag(workspaceId, command.title(), command.hexColor());
        tagCommandPort.save(newTag);

        return TagResponseMapper.toResponse(newTag);
    }

    private void validateTitleUniqueness(String title, WorkspaceId workspaceId) {
        if (tagQueryPort.existsByTitleAndWorkspace(title.trim().toLowerCase(Locale.ROOT), workspaceId)) {
            throw new DuplicateTagTitleException(title, workspaceId);
        }
    }
}

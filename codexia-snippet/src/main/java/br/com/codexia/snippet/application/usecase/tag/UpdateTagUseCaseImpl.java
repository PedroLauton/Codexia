package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.UpdateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.input.tag.UpdateTagUseCase;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.TagResponseMapper;
import br.com.codexia.snippet.domain.exception.DuplicateTagTitleException;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;

import java.util.Locale;

public class UpdateTagUseCaseImpl implements UpdateTagUseCase {

    private final TagCommandPort tagCommandPort;
    private final TagQueryPort tagQueryPort;

    public UpdateTagUseCaseImpl(TagCommandPort tagCommandPort, TagQueryPort tagQueryPort) {
        this.tagCommandPort = tagCommandPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public TagResponse execute(UpdateTagCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        TagId tagId = TagId.fromString(command.tagId());

        Tag tag = findTagOrThrow(tagId, workspaceId);
        validateTitleUniqueness(command.title(), workspaceId, tagId);

        tag.updateMetadata(command.title(), command.hexColor());

        tagCommandPort.save(tag);

        return TagResponseMapper.toResponse(tag);
    }

    private Tag findTagOrThrow(TagId tagId, WorkspaceId workspaceId) {
        return tagQueryPort.findById(tagId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag with id: " + tagId.value() + " not found"));
    }

    private void validateTitleUniqueness(String title, WorkspaceId workspaceId, TagId tagId) {
        if (tagQueryPort.existsByTitleAndWorkspace(title.trim().toLowerCase(Locale.ROOT), workspaceId, tagId)) {
            throw new DuplicateTagTitleException(title, workspaceId);
        }
    }
}

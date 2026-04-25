package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.AddSnippetVersionCommand;
import br.com.codexia.snippet.application.dto.response.SnippetVersionAddedResponse;
import br.com.codexia.snippet.application.ports.input.AddSnippetVersionUseCase;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.SnippetResponseMapper;
import br.com.codexia.snippet.domain.model.*;

public class AddSnippetVersionUseCaseImpl implements AddSnippetVersionUseCase {

    private final SnippetCommandPort snippetCommandPort;
    private final SnippetQueryPort snippetQueryPort;

    public AddSnippetVersionUseCaseImpl(SnippetCommandPort snippetCommandPort, SnippetQueryPort snippetQueryPort) {
        this.snippetCommandPort = snippetCommandPort;
        this.snippetQueryPort = snippetQueryPort;
    }

    @Override
    public SnippetVersionAddedResponse execute(AddSnippetVersionCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        SnippetId snippetId = SnippetId.fromString(command.snippetId());
        Language language = Language.fromString(command.language());

        Snippet snippet = findSnippetOrThrow(snippetId, workspaceId);

        SnippetVersion newVersion = snippet.addVersion(command.title(), command.description(), command.content(), language);

        snippetCommandPort.save(snippet);

        return SnippetResponseMapper.toVersionAddedResponse(snippet, newVersion);
    }

    private Snippet findSnippetOrThrow(SnippetId snippetId, WorkspaceId workspaceId) {
        return snippetQueryPort.findById(snippetId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet with id: " + snippetId + " not found"));
    }
}

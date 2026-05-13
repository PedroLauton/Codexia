package br.com.codexia.snippet.application.usecase.snippet;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.AddSnippetVersionCommand;
import br.com.codexia.snippet.application.dto.response.SnippetVersionAddedResponse;
import br.com.codexia.snippet.application.ports.input.snippet.AddSnippetVersionUseCase;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.usecase.mapper.SnippetResponseMapper;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.domain.model.*;

public class AddSnippetVersionUseCaseImpl implements AddSnippetVersionUseCase {

    private final SnippetCommandPort snippetCommandPort;
    private final SnippetFinder snippetFinder;

    public AddSnippetVersionUseCaseImpl(SnippetCommandPort snippetCommandPort,
                                        SnippetFinder snippetFinder) {
        this.snippetCommandPort = snippetCommandPort;
        this.snippetFinder = snippetFinder;
    }

    @Override
    public SnippetVersionAddedResponse execute(AddSnippetVersionCommand command) {
        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        SnippetId snippetId = SnippetId.fromString(command.snippetId());
        Language language = Language.fromString(command.language());

        Snippet snippet = snippetFinder.findActiveOrThrow(snippetId, workspaceId);

        SnippetVersion newVersion = snippet.addVersion(command.title(), command.description(), command.content(), language);

        snippetCommandPort.save(snippet);

        return SnippetResponseMapper.toVersionAddedResponse(snippet, newVersion);
    }
}

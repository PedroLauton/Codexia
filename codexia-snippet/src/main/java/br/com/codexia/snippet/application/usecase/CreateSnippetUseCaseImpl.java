package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetResponse;
import br.com.codexia.snippet.application.ports.input.CreateSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.SnippetResponseMapper;
import br.com.codexia.snippet.domain.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class CreateSnippetUseCaseImpl implements CreateSnippetUseCase {

    private SnippetCommandPort snippetCommandPort;
    private CategoryQueryPort categoryQueryPort;
    private TagQueryPort tagQueryPort;

    public CreateSnippetUseCaseImpl(SnippetCommandPort snippetCommandPort, CategoryQueryPort categoryQueryPort, TagQueryPort tagQueryPort) {
        this.snippetCommandPort = snippetCommandPort;
        this.categoryQueryPort = categoryQueryPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public SnippetResponse execute(CreateSnippetCommand command) {

        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        AccountId  accountId = AccountId.fromString(command.accountId());
        CategoryId categoryId = CategoryId.fromString(command.categoryId());
        Language language = Language.fromString(command.language());

        Set<TagId> tagIds = resolveTagIds(command.tagIds());

        validateCategory(categoryId, workspaceId);
        List<Tag> tags = validateAndResolveTags(tagIds, workspaceId);

        Snippet newSnippet = new Snippet(workspaceId, accountId, categoryId, tagIds,
                command.title(), command.description(), command.content(), language);

        snippetCommandPort.save(newSnippet);

        return SnippetResponseMapper.toResponse(newSnippet, tags);
    }

    private void validateCategory(CategoryId categoryId, WorkspaceId workspaceId) {
        if(!categoryQueryPort.existsById(categoryId, workspaceId)) {
            throw new ResourceNotFoundException("Category not found.");
        }
    }

    private List<Tag> validateAndResolveTags(Set<TagId> tagsIds, WorkspaceId workspaceId) {
        if(tagsIds.isEmpty()) return List.of();

        List<Tag> tags = tagQueryPort.findAllByIds(tagsIds, workspaceId);

        if(tags.size() != tagsIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found.");

        }

        return tags;
    }

    private Set<TagId> resolveTagIds(Set<String> rawIds) {
        if(rawIds == null || rawIds.isEmpty()) {
            return Collections.emptySet();
        }

        return rawIds.stream()
                .map(TagId::fromString)
                .collect(Collectors.toUnmodifiableSet());
    }
}

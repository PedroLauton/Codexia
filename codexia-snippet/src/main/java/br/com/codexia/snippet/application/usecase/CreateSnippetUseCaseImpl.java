package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetResponse;
import br.com.codexia.snippet.application.ports.input.CreateSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.CategoryRepositoryPort;
import br.com.codexia.snippet.application.ports.output.SnippetRepositoryPort;
import br.com.codexia.snippet.application.ports.output.TagRepositoryPort;
import br.com.codexia.snippet.application.usecase.mapper.SnippetResponseMapper;
import br.com.codexia.snippet.domain.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class CreateSnippetUseCaseImpl implements CreateSnippetUseCase {

    private SnippetRepositoryPort snippetRepositoryPort;
    private CategoryRepositoryPort categoryRepositoryPort;
    private TagRepositoryPort tagRepositoryPort;

    public CreateSnippetUseCaseImpl(SnippetRepositoryPort snippetRepositoryPort, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort) {
        this.snippetRepositoryPort = snippetRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
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

        snippetRepositoryPort.save(newSnippet);

        return SnippetResponseMapper.toResponse(newSnippet, tags);
    }

    private void validateCategory(CategoryId categoryId, WorkspaceId workspaceId) {
        if(!categoryRepositoryPort.existsById(categoryId, workspaceId)) {
            throw new ResourceNotFoundException("Category not found.");
        }
    }

    private List<Tag> validateAndResolveTags(Set<TagId> tagsIds, WorkspaceId workspaceId) {
        if(tagsIds.isEmpty()) return List.of();

        List<Tag> tags = tagRepositoryPort.findAllByIds(tagsIds, workspaceId);

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

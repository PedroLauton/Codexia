package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.ReassignSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetReassignedResponse;
import br.com.codexia.snippet.application.ports.input.ReassignSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.mapper.SnippetResponseMapper;
import br.com.codexia.snippet.domain.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReassignSnippetUseCaseImpl implements ReassignSnippetUseCase {

    private final SnippetCommandPort snippetCommandPort;
    private final SnippetQueryPort snippetQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final TagQueryPort tagQueryPort;

    public ReassignSnippetUseCaseImpl(SnippetCommandPort snippetCommandPort, SnippetQueryPort snippetQueryPort, CategoryQueryPort categoryQueryPort, TagQueryPort tagQueryPort) {
        this.snippetCommandPort = snippetCommandPort;
        this.snippetQueryPort = snippetQueryPort;
        this.categoryQueryPort = categoryQueryPort;
        this.tagQueryPort = tagQueryPort;
    }

    @Override
    public SnippetReassignedResponse execute(ReassignSnippetCommand command) {

        WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
        SnippetId snippetId = SnippetId.fromString(command.snippetId());

        Snippet snippet = findSnippetOrThrow(snippetId, workspaceId);

        reassignCategoryIfChanged(snippet, command.categoryId(), workspaceId);
        List<Tag> resolvedTags = reconcileTags(snippet, command.tagIds(), workspaceId);

        snippetCommandPort.save(snippet);

        return SnippetResponseMapper.toReassignedResponse(snippet, resolvedTags);
    }

    private void reassignCategoryIfChanged(Snippet snippet, String rawCategoryId, WorkspaceId workspaceId) {
        CategoryId newCategoryId = CategoryId.fromString(rawCategoryId);
        if(newCategoryId.equals(snippet.getCategoryId())) return;

        validateCategory(newCategoryId, workspaceId);
        snippet.assignToCategory(newCategoryId);
    }

    private List<Tag> reconcileTags(Snippet snippet, Set<String> rawTagIds, WorkspaceId workspaceId) {
        Set<TagId> desiredTagIds = rawTagIds.stream()
                .map(TagId::fromString)
                .collect(Collectors.toUnmodifiableSet());

        List<Tag> allDesiredTags = tagQueryPort.findAllByIds(desiredTagIds, workspaceId);

        validateAllTagsFound(desiredTagIds, allDesiredTags);

        Set<TagId> currentTagIds = new HashSet<>(snippet.getTagIds());
        currentTagIds.stream()
                .filter(id -> !desiredTagIds.contains(id))
                .forEach(snippet::unlinkTag);

        allDesiredTags.stream()
                .filter(tag -> !currentTagIds.contains(tag.getId()))
                .forEach(tag -> snippet.linkTag(tag.getId()));

        return allDesiredTags;
    }

    private Snippet findSnippetOrThrow(SnippetId snippetId, WorkspaceId workspaceId) {
        return snippetQueryPort.findById(snippetId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet with id: " + snippetId + " not found"));
    }

    private void validateCategory(CategoryId categoryId, WorkspaceId workspaceId) {
        if(!categoryQueryPort.existsById(categoryId, workspaceId)) {
            throw new ResourceNotFoundException("Category with id: " + categoryId + " not found");
        }
    }

    private void validateAllTagsFound(Set<TagId> desiredIds, List<Tag> foundTags) {
        if (foundTags.size() != desiredIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found.");
        }
    }
}

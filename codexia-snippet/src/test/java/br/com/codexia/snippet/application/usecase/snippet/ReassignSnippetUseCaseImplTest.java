package br.com.codexia.snippet.application.usecase.snippet;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.ReassignSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetReassignedResponse;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.domain.model.aggregate.Snippet;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
import br.com.codexia.snippet.domain.model.valueobject.SnippetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReassignSnippetUseCase")
class ReassignSnippetUseCaseImplTest {

    @Mock
    private SnippetCommandPort snippetCommandPort;

    @Mock
    private SnippetFinder snippetFinder;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @Mock
    private TagQueryPort tagQueryPort;

    private ReassignSnippetUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReassignSnippetUseCaseImpl(snippetCommandPort, snippetFinder, categoryQueryPort, tagQueryPort);
    }

    @Nested
    @DisplayName("when reassigning to root (null categoryId)")
    class WhenReassigningToRoot {

        @Test
        @DisplayName("should move snippet to root and return null categoryId")
        void shouldMoveSnippetToRoot() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            CategoryId existingCategoryId = CategoryId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId, existingCategoryId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);
            when(tagQueryPort.findAllByIds(any(), any())).thenReturn(List.of());

            ReassignSnippetCommand command = new ReassignSnippetCommand(
                    snippetId.value().toString(), workspaceId, null, Set.of());

            SnippetReassignedResponse response = useCase.execute(command);

            assertThat(response.categoryId()).isNull();
            verify(snippetCommandPort).save(snippet);
            verifyNoInteractions(categoryQueryPort);
        }
    }

    @Nested
    @DisplayName("when categoryId is unchanged")
    class WhenCategoryIdIsUnchanged {

        @Test
        @DisplayName("should skip category validation when categoryId matches current")
        void shouldSkipValidationWhenCategoryUnchanged() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            CategoryId categoryId = CategoryId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId, categoryId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);
            when(tagQueryPort.findAllByIds(any(), any())).thenReturn(List.of());

            ReassignSnippetCommand command = new ReassignSnippetCommand(
                    snippetId.value().toString(), workspaceId, categoryId.value().toString(), Set.of());

            useCase.execute(command);

            verify(categoryQueryPort, never()).existsById(any(), any());
            verify(snippetCommandPort).save(snippet);
        }
    }

    @Nested
    @DisplayName("when reassigning to a new category")
    class WhenReassigningToNewCategory {

        @Test
        @DisplayName("should assign new category when it exists")
        void shouldAssignNewCategory() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            CategoryId oldCategoryId = CategoryId.generate();
            CategoryId newCategoryId = CategoryId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId, oldCategoryId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);
            when(categoryQueryPort.existsById(newCategoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(true);
            when(tagQueryPort.findAllByIds(any(), any())).thenReturn(List.of());

            ReassignSnippetCommand command = new ReassignSnippetCommand(
                    snippetId.value().toString(), workspaceId, newCategoryId.value().toString(), Set.of());

            SnippetReassignedResponse response = useCase.execute(command);

            assertThat(response.categoryId()).isEqualTo(newCategoryId.value().toString());
            verify(snippetCommandPort).save(snippet);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when new category does not exist")
        void shouldThrowWhenCategoryNotFound() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            CategoryId oldCategoryId = CategoryId.generate();
            CategoryId newCategoryId = CategoryId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId, oldCategoryId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);
            when(categoryQueryPort.existsById(newCategoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(false);

            ReassignSnippetCommand command = new ReassignSnippetCommand(
                    snippetId.value().toString(), workspaceId, newCategoryId.value().toString(), Set.of());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(snippetCommandPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when snippet does not exist")
    class WhenSnippetDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException")
        void shouldThrow() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();

            when(snippetFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Snippet not found"));

            ReassignSnippetCommand command = new ReassignSnippetCommand(
                    snippetId.value().toString(), workspaceId, null, Set.of());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(snippetCommandPort);
        }
    }

    private Snippet buildSnippet(SnippetId id, String workspaceId, CategoryId categoryId) {
        return new Snippet(
                id,
                WorkspaceId.fromString(workspaceId),
                AccountId.fromString(UUID.randomUUID().toString()),
                categoryId,
                Set.of(),
                Set.of(),
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

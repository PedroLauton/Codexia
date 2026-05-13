package br.com.codexia.snippet.application.usecase.snippet;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteSnippetCommand;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.application.usecase.snippet.DeleteSnippetUseCaseImpl;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.SnippetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteSnippetUseCase")
class DeleteSnippetUseCaseImplTest {

    @Mock
    private SnippetCommandPort snippetCommandPort;

    @Mock
    private SnippetFinder snippetFinder;

    private DeleteSnippetUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteSnippetUseCaseImpl(snippetCommandPort, snippetFinder);
    }

    @Nested
    @DisplayName("when snippet is deleted successfully")
    class WhenSnippetIsDeletedSuccessfully {

        @Test
        @DisplayName("should delete snippet")
        void shouldDeleteSnippet() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);

            DeleteSnippetCommand command = buildCommand(snippetId.value().toString(), workspaceId);

            assertThatNoException().isThrownBy(() -> useCase.execute(command));
            verify(snippetCommandPort).save(snippet);
        }
    }

    @Nested
    @DisplayName("when snippet is not found")
    class WhenSnippetIsNotFound {

        @Test
        @DisplayName("should throw ResourceNotFoundException when snippet does not exist")
        void shouldThrowWhenSnippetDoesNotExist() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();

            when(snippetFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Snippet not found"));

            DeleteSnippetCommand command = buildCommand(snippetId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(snippetCommandPort);
        }
    }

    private DeleteSnippetCommand buildCommand(String snippetId, String workspaceId) {
        return new DeleteSnippetCommand(snippetId, workspaceId);
    }

    private Snippet buildSnippet(SnippetId id, String workspaceId) {
        return new Snippet(
                id,
                WorkspaceId.fromString(workspaceId),
                AccountId.fromString(UUID.randomUUID().toString()),
                CategoryId.generate(),
                Set.of(),
                Set.of(),
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

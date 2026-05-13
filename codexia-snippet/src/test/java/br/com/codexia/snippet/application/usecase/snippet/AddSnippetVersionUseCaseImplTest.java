package br.com.codexia.snippet.application.usecase.snippet;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.AddSnippetVersionCommand;
import br.com.codexia.snippet.application.dto.response.SnippetVersionAddedResponse;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.application.usecase.snippet.AddSnippetVersionUseCaseImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddSnippetVersionUseCase")
class AddSnippetVersionUseCaseImplTest {

    @Mock
    private SnippetCommandPort snippetCommandPort;

    @Mock
    private SnippetFinder snippetFinder;

    private AddSnippetVersionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddSnippetVersionUseCaseImpl(snippetCommandPort, snippetFinder);
    }

    @Nested
    @DisplayName("when version is added successfully")
    class WhenVersionIsAddedSuccessfully {

        @Test
        @DisplayName("should add version to snippet and return response")
        void shouldAddVersionToSnippet() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();
            Snippet snippet = buildSnippet(snippetId, workspaceId);

            when(snippetFinder.findActiveOrThrow(snippetId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(snippet);

            AddSnippetVersionCommand command = buildCommand(snippetId.value().toString(), workspaceId, "JAVA");

            SnippetVersionAddedResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            assertThat(response.snippetId()).isEqualTo(snippetId.value().toString());
            verify(snippetCommandPort).save(snippet);
        }
    }

    @Nested
    @DisplayName("when language is invalid")
    class WhenLanguageIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException before reaching the finder when language is unknown")
        void shouldThrowWhenLanguageIsUnknown() {
            String workspaceId = UUID.randomUUID().toString();
            SnippetId snippetId = SnippetId.generate();

            AddSnippetVersionCommand command = buildCommand(snippetId.value().toString(), workspaceId, "cobol");

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(snippetFinder, snippetCommandPort);
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

            AddSnippetVersionCommand command = buildCommand(snippetId.value().toString(), workspaceId, "JAVA");

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(snippetCommandPort);
        }
    }

    private AddSnippetVersionCommand buildCommand(String snippetId, String workspaceId, String language) {
        return new AddSnippetVersionCommand(snippetId, workspaceId, "Version Title", "Description", "System.out.println();", language);
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

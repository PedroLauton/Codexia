package br.com.codexia.snippet.application.usecase;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetResponse;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSnippetUseCase")
public class CreateSnippetUseCaseImplTest {

    @Mock
    private SnippetCommandPort  snippetCommandPort;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @Mock
    private TagQueryPort tagQueryPort;

    @InjectMocks
    private CreateSnippetUseCaseImpl useCase;

    private CreateSnippetCommand buildCommand(Set<String> tagIds) {
        return new CreateSnippetCommand(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                tagIds,
                "Bubble Sort",
                "Algoritmo de ordenação",
                "for(int i...",
                "JAVA"
        );
    }

    private Tag buildTag(WorkspaceId workspaceId, TagId tagId) {
        return new Tag(
                tagId,
                workspaceId,
                "Algoritmos",
                "#FF0000",
                Instant.now(),
                null
        );
    }

    @Nested
    @DisplayName("When all inputs are valid")
    class WhenAllInputsAreValid {

        private CreateSnippetCommand command = buildCommand(Set.of());

        @Test
        @DisplayName("Should create snippet without tags")
        void shouldCreateSnippetWithoutTags() {
            // Arrange
            when(categoryQueryPort.existsById(
                    CategoryId.fromString(command.categoryId()),
                    WorkspaceId.fromString(command.workspaceId())))
                    .thenReturn(true);

            // Act
            SnippetResponse response = useCase.execute(command);

            // Assert
            assertThat(response).isNotNull(); // Response não pode ser nulo
            assertThat(response.tags()).isEmpty(); // Tags devem estar vazias
            verify(snippetCommandPort, times(1)).save(any()); // Verifica se o .save foi chamada apenas uma única vez
            verifyNoInteractions(tagQueryPort); // Como não há tags, o tagQueryPort não deve ser chamado
        }

        @Test
        @DisplayName("Should create snippet with tags")
        void shouldCreateSnippetWithTags() {
            // Arrange
            TagId tagId = TagId.generate();
            command = buildCommand(Set.of(tagId.value().toString()));

            WorkspaceId workspaceId = WorkspaceId.fromString(command.workspaceId());
            Tag tag = buildTag(workspaceId, tagId);

            when(categoryQueryPort.existsById(
                    CategoryId.fromString(command.categoryId()),
                    workspaceId))
                    .thenReturn(true);
            when(tagQueryPort.findAllByIds(any(), any())).thenReturn(List.of(tag));

            // Act
            SnippetResponse response = useCase.execute(command);

            // Assert
            assertThat(response).isNotNull(); // Response não pode ser nulo
            assertThat(response.tags()).hasSize(1); // Tags devem ter 1 elemento
            verify(snippetCommandPort, times(1)).save(any()); // Verifica se o .save foi chamada apenas uma única vez
            verify(tagQueryPort, times(1)).findAllByIds(any(), any()); // Verifica se o .findAllByIds foi chamada apenas uma única vez
        }
    }

    @Nested
    @DisplayName("When category does not exist")
    class WhenCategoryDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException and never persist")
        void shouldThrowAndNeverPersist() {

            // Arrange
            CreateSnippetCommand command =  buildCommand(Set.of());
            when(categoryQueryPort.existsById(CategoryId.fromString(command.categoryId()), WorkspaceId.fromString(command.workspaceId()))).thenReturn(false);

            // Act
            assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(ResourceNotFoundException.class);

            // Assert
            verifyNoInteractions(snippetCommandPort);
            verifyNoInteractions(tagQueryPort);
        }
    }

    @Nested
    @DisplayName("When tags do not exist")
    class WhenTagsDoNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException and never persist when tags do not exist")
        void shouldThrowAndNeverPersistWhenTagsNotFound() {
            // Arrange
            TagId tagId = TagId.generate();
            CreateSnippetCommand command = buildCommand(Set.of(tagId.value().toString()));

            when(categoryQueryPort.existsById(CategoryId.fromString(command.categoryId()), WorkspaceId.fromString(command.workspaceId()))).thenReturn(true);
            when(tagQueryPort.findAllByIds(any(), any())).thenReturn(List.of());

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(ResourceNotFoundException.class);

            // Assert
            verifyNoInteractions(snippetCommandPort);
            verify(tagQueryPort, times(1)).findAllByIds(any(), any());
        }
    }

    @Nested
    @DisplayName("When language is invalid")
    class WhenLanguageIsInvalid {

        @Test
        @DisplayName("Should throw IllegalArgumentException")
        void shouldThrow() {
            CreateSnippetCommand command = new CreateSnippetCommand(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    Set.of(),
                    "Bubble Sort",
                    "Descrição",
                    "código",
                    "jsjkef" // não existe no enum Language
            );

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(snippetCommandPort);
            verifyNoInteractions(categoryQueryPort);
            verifyNoInteractions(tagQueryPort);
        }
    }
}

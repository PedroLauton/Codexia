package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.UpdateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.application.usecase.tag.UpdateTagUseCaseImpl;
import br.com.codexia.snippet.domain.exception.DuplicateTagTitleException;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTagUseCase")
class UpdateTagUseCaseImplTest {

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagFinder tagFinder;

    @Mock
    private TagQueryPort tagQueryPort;

    private UpdateTagUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateTagUseCaseImpl(tagCommandPort, tagFinder, tagQueryPort);
    }

    @Nested
    @DisplayName("when tag is updated successfully")
    class WhenTagIsUpdatedSuccessfully {

        @Test
        @DisplayName("should update tag metadata and return response")
        void shouldUpdateTagMetadata() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();
            Tag tag = buildTag(tagId, workspaceId);

            when(tagFinder.findActiveOrThrow(tagId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(tag);
            when(tagQueryPort.existsByTitleAndWorkspace("python", WorkspaceId.fromString(workspaceId), tagId))
                    .thenReturn(false);

            UpdateTagCommand command = buildCommand(tagId.value().toString(), workspaceId, "Python", "#00FF00");

            TagResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            verify(tagCommandPort).save(tag);
        }
    }

    @Nested
    @DisplayName("when tag title is duplicated")
    class WhenTagTitleIsDuplicated {

        @Test
        @DisplayName("should throw DuplicateTagTitleException when title already exists in workspace")
        void shouldThrowWhenTitleAlreadyExistsInWorkspace() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();
            Tag tag = buildTag(tagId, workspaceId);

            when(tagFinder.findActiveOrThrow(tagId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(tag);
            when(tagQueryPort.existsByTitleAndWorkspace("python", WorkspaceId.fromString(workspaceId), tagId))
                    .thenReturn(true);

            UpdateTagCommand command = buildCommand(tagId.value().toString(), workspaceId, "Python", "#00FF00");

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(DuplicateTagTitleException.class);

            verifyNoInteractions(tagCommandPort);
        }
    }

    @Nested
    @DisplayName("when tag is not found")
    class WhenTagIsNotFound {

        @Test
        @DisplayName("should throw ResourceNotFoundException when tag does not exist")
        void shouldThrowWhenTagDoesNotExist() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();

            when(tagFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Tag not found"));

            UpdateTagCommand command = buildCommand(tagId.value().toString(), workspaceId, "Python", "#00FF00");

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(tagQueryPort, tagCommandPort);
        }
    }

    private UpdateTagCommand buildCommand(String tagId, String workspaceId, String title, String hexColor) {
        return new UpdateTagCommand(tagId, workspaceId, title, hexColor);
    }

    private Tag buildTag(TagId id, String workspaceId) {
        return new Tag(
                id,
                WorkspaceId.fromString(workspaceId),
                "java",
                "#FF0000",
                Instant.now(),
                null
        );
    }
}

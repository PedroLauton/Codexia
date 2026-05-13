package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteTagCommand;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.application.usecase.tag.DeleteTagUseCaseImpl;
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

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteTagUseCase")
class DeleteTagUseCaseImplTest {

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagFinder tagFinder;

    private DeleteTagUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteTagUseCaseImpl(tagCommandPort, tagFinder);
    }

    @Nested
    @DisplayName("when tag is deleted successfully")
    class WhenTagIsDeletedSuccessfully {

        @Test
        @DisplayName("should delete tag")
        void shouldDeleteTag() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();
            Tag tag = buildTag(tagId, workspaceId);

            when(tagFinder.findActiveOrThrow(tagId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(tag);

            DeleteTagCommand command = buildCommand(tagId.value().toString(), workspaceId);

            assertThatNoException().isThrownBy(() -> useCase.execute(command));
            verify(tagCommandPort).save(tag);
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

            DeleteTagCommand command = buildCommand(tagId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(tagCommandPort);
        }
    }

    private DeleteTagCommand buildCommand(String tagId, String workspaceId) {
        return new DeleteTagCommand(tagId, workspaceId);
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

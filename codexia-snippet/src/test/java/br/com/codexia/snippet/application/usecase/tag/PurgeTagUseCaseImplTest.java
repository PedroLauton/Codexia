package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.PurgeTagCommand;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;
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
@DisplayName("PurgeTagUseCase")
class PurgeTagUseCaseImplTest {

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagFinder tagFinder;

    private PurgeTagUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new PurgeTagUseCaseImpl(tagCommandPort, tagFinder);
    }

    @Nested
    @DisplayName("when tag is purged successfully")
    class WhenTagIsPurgedSuccessfully {

        @Test
        @DisplayName("should purge deleted tag")
        void shouldPurgeDeletedTag() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();
            Tag deletedTag = buildDeletedTag(tagId, workspaceId);

            when(tagFinder.findDeletedOrThrow(tagId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(deletedTag);

            PurgeTagCommand command = buildCommand(tagId.value().toString(), workspaceId);

            assertThatNoException().isThrownBy(() -> useCase.execute(command));
            verify(tagCommandPort).delete(tagId);
        }
    }

    @Nested
    @DisplayName("when deleted tag is not found")
    class WhenTagIsNotFound {

        @Test
        @DisplayName("should throw ResourceNotFoundException when deleted tag does not exist")
        void shouldThrowWhenDeletedTagDoesNotExist() {
            String workspaceId = UUID.randomUUID().toString();
            TagId tagId = TagId.generate();

            when(tagFinder.findDeletedOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Deleted tag not found"));

            PurgeTagCommand command = buildCommand(tagId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(tagCommandPort);
        }
    }

    private PurgeTagCommand buildCommand(String tagId, String workspaceId) {
        return new PurgeTagCommand(tagId, workspaceId);
    }

    private Tag buildDeletedTag(TagId id, String workspaceId) {
        return new Tag(
                id,
                WorkspaceId.fromString(workspaceId),
                "java",
                "#FF0000",
                Instant.now(),
                Instant.now()
        );
    }
}

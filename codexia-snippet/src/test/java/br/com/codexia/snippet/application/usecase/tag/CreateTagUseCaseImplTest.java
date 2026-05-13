package br.com.codexia.snippet.application.usecase.tag;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.CreateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.tag.CreateTagUseCaseImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTagUseCase")
class CreateTagUseCaseImplTest {

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagQueryPort tagQueryPort;

    private CreateTagUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateTagUseCaseImpl(tagCommandPort, tagQueryPort);
    }

    @Nested
    @DisplayName("when tag is created successfully")
    class WhenTagIsCreatedSuccessfully {

        @Test
        @DisplayName("should create tag and return response")
        void shouldCreateTag() {
            String workspaceId = UUID.randomUUID().toString();

            when(tagQueryPort.existsByTitleAndWorkspace("java", WorkspaceId.fromString(workspaceId)))
                    .thenReturn(false);

            CreateTagCommand command = buildCommand(workspaceId, "Java", "#FF0000");

            TagResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            verify(tagCommandPort).save(any(Tag.class));
        }
    }

    @Nested
    @DisplayName("when tag title is duplicated")
    class WhenTagTitleIsDuplicated {

        @Test
        @DisplayName("should throw DuplicateTagTitleException when title already exists in workspace")
        void shouldThrowWhenTitleAlreadyExistsInWorkspace() {
            String workspaceId = UUID.randomUUID().toString();

            when(tagQueryPort.existsByTitleAndWorkspace("java", WorkspaceId.fromString(workspaceId)))
                    .thenReturn(true);

            CreateTagCommand command = buildCommand(workspaceId, "Java", "#FF0000");

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(DuplicateTagTitleException.class);

            verifyNoInteractions(tagCommandPort);
        }
    }

    private CreateTagCommand buildCommand(String workspaceId, String title, String hexColor) {
        return new CreateTagCommand(workspaceId, title, hexColor);
    }

    private Tag buildTag(String workspaceId) {
        return new Tag(
                TagId.generate(),
                WorkspaceId.fromString(workspaceId),
                "java",
                "#FF0000",
                Instant.now(),
                null
        );
    }
}

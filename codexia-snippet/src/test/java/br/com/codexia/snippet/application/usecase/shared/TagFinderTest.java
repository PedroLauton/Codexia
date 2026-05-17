package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagFinder")
class TagFinderTest {

    @Mock
    private TagQueryPort tagQueryPort;

    private TagFinder finder;

    @BeforeEach
    void setUp() {
        finder = new TagFinder(tagQueryPort);
    }

    @Nested
    @DisplayName("findActiveOrThrow")
    class FindActiveOrThrow {

        @Test
        @DisplayName("should return tag when found")
        void shouldReturnTagWhenFound() {
            TagId id = TagId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());
            Tag tag = buildTag(id, workspaceId);

            when(tagQueryPort.findById(id, workspaceId)).thenReturn(Optional.of(tag));

            Tag result = finder.findActiveOrThrow(id, workspaceId);

            assertThat(result).isSameAs(tag);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            TagId id = TagId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());

            when(tagQueryPort.findById(id, workspaceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> finder.findActiveOrThrow(id, workspaceId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findDeletedOrThrow")
    class FindDeletedOrThrow {

        @Test
        @DisplayName("should return deleted tag when found")
        void shouldReturnDeletedTagWhenFound() {
            TagId id = TagId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());
            Tag deletedTag = buildDeletedTag(id, workspaceId);

            when(tagQueryPort.findDeletedById(id, workspaceId)).thenReturn(Optional.of(deletedTag));

            Tag result = finder.findDeletedOrThrow(id, workspaceId);

            assertThat(result).isSameAs(deletedTag);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            TagId id = TagId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());

            when(tagQueryPort.findDeletedById(id, workspaceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> finder.findDeletedOrThrow(id, workspaceId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private Tag buildTag(TagId id, WorkspaceId workspaceId) {
        return new Tag(id, workspaceId, "test-tag", "#FF0000", Instant.now(), null);
    }

    private Tag buildDeletedTag(TagId id, WorkspaceId workspaceId) {
        return new Tag(id, workspaceId, "test-tag", "#FF0000", Instant.now(), Instant.now());
    }
}

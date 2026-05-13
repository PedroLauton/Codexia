package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SnippetFinder")
class SnippetFinderTest {

    @Mock
    private SnippetQueryPort snippetQueryPort;

    private SnippetFinder finder;

    @BeforeEach
    void setUp() {
        finder = new SnippetFinder(snippetQueryPort);
    }

    @Nested
    @DisplayName("findActiveOrThrow")
    class FindActiveOrThrow {

        @Test
        @DisplayName("should return snippet when found")
        void shouldReturnSnippetWhenFound() {
            SnippetId id = SnippetId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());
            Snippet snippet = buildSnippet(id, workspaceId);

            when(snippetQueryPort.findById(id, workspaceId)).thenReturn(Optional.of(snippet));

            Snippet result = finder.findActiveOrThrow(id, workspaceId);

            assertThat(result).isSameAs(snippet);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            SnippetId id = SnippetId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());

            when(snippetQueryPort.findById(id, workspaceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> finder.findActiveOrThrow(id, workspaceId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private Snippet buildSnippet(SnippetId id, WorkspaceId workspaceId) {
        return new Snippet(
                id,
                workspaceId,
                AccountId.fromString(UUID.randomUUID().toString()),
                null,
                Set.of(),
                Set.of(),
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

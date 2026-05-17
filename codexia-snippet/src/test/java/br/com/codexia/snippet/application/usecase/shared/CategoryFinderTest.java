package br.com.codexia.snippet.application.usecase.shared;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
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
@DisplayName("CategoryFinder")
class CategoryFinderTest {

    @Mock
    private CategoryQueryPort categoryQueryPort;

    private CategoryFinder finder;

    @BeforeEach
    void setUp() {
        finder = new CategoryFinder(categoryQueryPort);
    }

    @Nested
    @DisplayName("findActiveOrThrow")
    class FindActiveOrThrow {

        @Test
        @DisplayName("should return category when found")
        void shouldReturnCategoryWhenFound() {
            CategoryId id = CategoryId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());
            Category category = buildCategory(id, workspaceId);

            when(categoryQueryPort.findById(id, workspaceId)).thenReturn(Optional.of(category));

            Category result = finder.findActiveOrThrow(id, workspaceId);

            assertThat(result).isSameAs(category);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            CategoryId id = CategoryId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());

            when(categoryQueryPort.findById(id, workspaceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> finder.findActiveOrThrow(id, workspaceId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findDeletedOrThrow")
    class FindDeletedOrThrow {

        @Test
        @DisplayName("should return deleted category when found")
        void shouldReturnDeletedCategoryWhenFound() {
            CategoryId id = CategoryId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());
            Category deletedCategory = buildDeletedCategory(id, workspaceId);

            when(categoryQueryPort.findDeletedById(id, workspaceId)).thenReturn(Optional.of(deletedCategory));

            Category result = finder.findDeletedOrThrow(id, workspaceId);

            assertThat(result).isSameAs(deletedCategory);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            CategoryId id = CategoryId.generate();
            WorkspaceId workspaceId = WorkspaceId.fromString(UUID.randomUUID().toString());

            when(categoryQueryPort.findDeletedById(id, workspaceId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> finder.findDeletedOrThrow(id, workspaceId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private Category buildCategory(CategoryId id, WorkspaceId workspaceId) {
        return new Category(id, workspaceId, "Test", null, null, 0,
                Instant.now(), Instant.now(), null);
    }

    private Category buildDeletedCategory(CategoryId id, WorkspaceId workspaceId) {
        return new Category(id, workspaceId, "Test", null, null, 0,
                Instant.now(), Instant.now(), Instant.now());
    }
}

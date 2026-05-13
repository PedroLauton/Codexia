package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.ReparentCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.category.ReparentCategoryUseCaseImpl;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.CategoryCircularReferenceException;
import br.com.codexia.snippet.domain.exception.CategoryMaxDepthExceededException;
import br.com.codexia.snippet.domain.exception.CategorySelfReferenceException;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
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
@DisplayName("ReparentCategoryUseCase")
class ReparentCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    private ReparentCategoryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReparentCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Nested
    @DisplayName("when moving to root")
    class WhenMovingToRoot {

        @Test
        @DisplayName("should set depth 0 and null parentId, then cascade")
        void shouldReparentToRoot() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId, CategoryId.generate(), 3);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, null);

            CategoryResponse response = useCase.execute(command);

            assertThat(response.parentId()).isNull();
            assertThat(response.depth()).isZero();
            verify(categoryCommandPort).save(category);
            verify(categoryCommandPort).updateSubtreeDepth(categoryId, 0, WorkspaceId.fromString(workspaceId));
        }
    }

    @Nested
    @DisplayName("when reparenting to a new parent")
    class WhenReparentingToNewParent {

        @Test
        @DisplayName("should set correct depth and cascade when parent exists at depth 2")
        void shouldReparentToNewParent() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            CategoryId newParentId = CategoryId.generate();

            Category category = buildCategory(categoryId, workspaceId, null, 0);
            Category parent = buildCategory(newParentId, workspaceId, null, 2);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.isAncestorOf(categoryId, newParentId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(false);
            when(categoryFinder.findActiveOrThrow(newParentId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(parent);

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, newParentId.value().toString());

            CategoryResponse response = useCase.execute(command);

            assertThat(response.depth()).isEqualTo(3);
            assertThat(response.parentId()).isEqualTo(newParentId.value().toString());
            verify(categoryCommandPort).save(category);
            verify(categoryCommandPort).updateSubtreeDepth(categoryId, 3, WorkspaceId.fromString(workspaceId));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when parent does not exist")
        void shouldThrowWhenParentNotFound() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            CategoryId newParentId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId, null, 0);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.isAncestorOf(any(), any(), any())).thenReturn(false);
            when(categoryFinder.findActiveOrThrow(newParentId, WorkspaceId.fromString(workspaceId)))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, newParentId.value().toString());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("should throw CategoryMaxDepthExceededException when parent is at max depth")
        void shouldThrowWhenMaxDepthExceeded() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            CategoryId newParentId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId, null, 0);
            Category parent = buildCategory(newParentId, workspaceId, null, 5);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.isAncestorOf(any(), any(), any())).thenReturn(false);
            when(categoryFinder.findActiveOrThrow(newParentId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(parent);

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, newParentId.value().toString());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategoryMaxDepthExceededException.class);

            verify(categoryCommandPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when circular reference or self-reference is detected")
    class WhenCircularOrSelfReference {

        @Test
        @DisplayName("should throw CategorySelfReferenceException when parentId equals categoryId")
        void shouldThrowSelfReference() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId, null, 0);

            when(categoryFinder.findActiveOrThrow(any(), any())).thenReturn(category);

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, categoryId.value().toString());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategorySelfReferenceException.class);

            verify(categoryCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("should throw CategoryCircularReferenceException when isAncestorOf returns true")
        void shouldThrowCircularReference() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            CategoryId newParentId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId, null, 0);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.isAncestorOf(categoryId, newParentId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(true);

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, newParentId.value().toString());

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategoryCircularReferenceException.class);

            verify(categoryCommandPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when category does not exist")
    class WhenCategoryDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException")
        void shouldThrow() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();

            when(categoryFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            ReparentCategoryCommand command = new ReparentCategoryCommand(
                    categoryId.value().toString(), workspaceId, null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryCommandPort, never()).save(any());
        }
    }

    private Category buildCategory(CategoryId id, String workspaceId, CategoryId parentId, int depth) {
        return new Category(
                id,
                WorkspaceId.fromString(workspaceId),
                "Category",
                null,
                parentId,
                depth,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

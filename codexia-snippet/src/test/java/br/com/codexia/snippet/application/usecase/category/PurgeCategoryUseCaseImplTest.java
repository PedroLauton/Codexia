package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.category.PurgeCategoryUseCaseImpl;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.CategoryHasChildrenException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PurgeCategoryUseCase")
class PurgeCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    private PurgeCategoryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new PurgeCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Nested
    @DisplayName("when category has children")
    class WhenCategoryHasChildren {

        @Test
        @DisplayName("should throw CategoryHasChildrenException and never delete")
        void shouldThrowWhenCategoryHasChildren() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category deletedCategory = buildDeletedCategory(categoryId, workspaceId);

            when(categoryFinder.findDeletedOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(deletedCategory);
            when(categoryQueryPort.hasChildren(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(true);

            DeleteCategoryCommand command = new DeleteCategoryCommand(
                    categoryId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategoryHasChildrenException.class);

            verify(categoryCommandPort, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("when category has no children")
    class WhenCategoryHasNoChildren {

        @Test
        @DisplayName("should purge successfully")
        void shouldPurgeSuccessfully() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category deletedCategory = buildDeletedCategory(categoryId, workspaceId);

            when(categoryFinder.findDeletedOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(deletedCategory);
            when(categoryQueryPort.hasChildren(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(false);

            DeleteCategoryCommand command = new DeleteCategoryCommand(
                    categoryId.value().toString(), workspaceId);

            assertThatNoException().isThrownBy(() -> useCase.execute(command));
            verify(categoryCommandPort).delete(categoryId);
        }
    }

    @Nested
    @DisplayName("when category is not soft-deleted")
    class WhenCategoryIsNotDeleted {

        @Test
        @DisplayName("should throw ResourceNotFoundException")
        void shouldThrowWhenNotSoftDeleted() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();

            when(categoryFinder.findDeletedOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Deleted category not found"));

            DeleteCategoryCommand command = new DeleteCategoryCommand(
                    categoryId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryCommandPort, never()).delete(any());
        }
    }

    private Category buildDeletedCategory(CategoryId id, String workspaceId) {
        return new Category(
                id,
                WorkspaceId.fromString(workspaceId),
                "Category",
                null,
                null,
                0,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
    }
}

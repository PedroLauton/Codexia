package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.DeleteCategoryCommand;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.CategoryHasActiveSnippetsException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCategoryUseCase")
class DeleteCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private SnippetQueryPort snippetQueryPort;

    private DeleteCategoryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteCategoryUseCaseImpl(categoryCommandPort, categoryFinder, snippetQueryPort);
    }

    @Nested
    @DisplayName("when category is deleted successfully")
    class WhenCategoryIsDeletedSuccessfully {

        @Test
        @DisplayName("should delete category when it has no active snippets")
        void shouldDeleteCategoryWithoutActiveSnippets() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(snippetQueryPort.existsActiveSnippetsByCategoryId(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(false);

            DeleteCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId);

            assertThatNoException().isThrownBy(() -> useCase.execute(command));
            verify(categoryCommandPort).save(category);
        }
    }

    @Nested
    @DisplayName("when category has active snippets")
    class WhenCategoryHasActiveSnippets {

        @Test
        @DisplayName("should throw CategoryHasActiveSnippetsException when category has active snippets")
        void shouldThrowWhenCategoryHasActiveSnippets() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(snippetQueryPort.existsActiveSnippetsByCategoryId(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(true);

            DeleteCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategoryHasActiveSnippetsException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }

    @Nested
    @DisplayName("when category is not found")
    class WhenCategoryIsNotFound {

        @Test
        @DisplayName("should throw ResourceNotFoundException when category does not exist")
        void shouldThrowWhenCategoryDoesNotExist() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();

            when(categoryFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            DeleteCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(snippetQueryPort, categoryCommandPort);
        }
    }

    private DeleteCategoryCommand buildCommand(String categoryId, String workspaceId) {
        return new DeleteCategoryCommand(categoryId, workspaceId);
    }

    private Category buildCategory(CategoryId id, String workspaceId) {
        return new Category(
                id,
                WorkspaceId.fromString(workspaceId),
                "Category",
                null,
                null,
                0,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

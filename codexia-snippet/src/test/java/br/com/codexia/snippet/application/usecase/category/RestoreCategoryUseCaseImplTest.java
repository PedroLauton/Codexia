package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.RestoreCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.usecase.category.RestoreCategoryUseCaseImpl;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
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
@DisplayName("RestoreCategoryUseCase")
class RestoreCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    private RestoreCategoryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RestoreCategoryUseCaseImpl(categoryCommandPort, categoryFinder);
    }

    @Nested
    @DisplayName("when category is restored successfully")
    class WhenCategoryIsRestoredSuccessfully {

        @Test
        @DisplayName("should restore deleted category and return response")
        void shouldRestoreDeletedCategory() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category deletedCategory = buildDeletedCategory(categoryId, workspaceId);

            when(categoryFinder.findDeletedOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(deletedCategory);

            RestoreCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId);

            CategoryResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(categoryId.value().toString());
            verify(categoryCommandPort).save(deletedCategory);
        }
    }

    @Nested
    @DisplayName("when deleted category is not found")
    class WhenCategoryIsNotFound {

        @Test
        @DisplayName("should throw ResourceNotFoundException when deleted category does not exist")
        void shouldThrowWhenDeletedCategoryDoesNotExist() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();

            when(categoryFinder.findDeletedOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Deleted category not found"));

            RestoreCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }

    private RestoreCategoryCommand buildCommand(String categoryId, String workspaceId) {
        return new RestoreCategoryCommand(categoryId, workspaceId);
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

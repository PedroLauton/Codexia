package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.dto.command.UpdateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.DuplicateCategoryNameException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCategoryUseCase")
class UpdateCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    private UpdateCategoryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Nested
    @DisplayName("when category is updated successfully")
    class WhenCategoryIsUpdatedSuccessfully {

        @Test
        @DisplayName("should update category metadata and return response")
        void shouldUpdateCategoryMetadata() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.existsByNameAndWorkspace("New Name", WorkspaceId.fromString(workspaceId), categoryId))
                    .thenReturn(false);

            UpdateCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId, "New Name", "New Desc");

            CategoryResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("New Name");
            verify(categoryCommandPort).save(category);
        }
    }

    @Nested
    @DisplayName("when category name is duplicated")
    class WhenCategoryNameIsDuplicated {

        @Test
        @DisplayName("should throw DuplicateCategoryNameException when name already exists in workspace")
        void shouldThrowWhenNameAlreadyExistsInWorkspace() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId categoryId = CategoryId.generate();
            Category category = buildCategory(categoryId, workspaceId);

            when(categoryFinder.findActiveOrThrow(categoryId, WorkspaceId.fromString(workspaceId)))
                    .thenReturn(category);
            when(categoryQueryPort.existsByNameAndWorkspace("Existing Name", WorkspaceId.fromString(workspaceId), categoryId))
                    .thenReturn(true);

            UpdateCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId, "Existing Name", null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(DuplicateCategoryNameException.class);

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

            UpdateCategoryCommand command = buildCommand(categoryId.value().toString(), workspaceId, "Name", null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(categoryQueryPort, categoryCommandPort);
        }
    }

    private UpdateCategoryCommand buildCommand(String categoryId, String workspaceId, String name, String description) {
        return new UpdateCategoryCommand(categoryId, workspaceId, name, description);
    }

    private Category buildCategory(CategoryId id, String workspaceId) {
        return new Category(
                id,
                WorkspaceId.fromString(workspaceId),
                "Original Name",
                null,
                null,
                0,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

package br.com.codexia.snippet.application.usecase;

import br.com.codexia.snippet.application.dto.command.CreateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.usecase.category.CreateCategoryUseCaseImpl;
import br.com.codexia.snippet.domain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCategoryUseCase")
class CreateCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @InjectMocks
    private CreateCategoryUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Nested
    @DisplayName("when all inputs are valid")
    class WhenAllInputsAreValid {

        @Test
        @DisplayName("should create category successfully")
        void shouldCreateCategorySuccessfully() {
            // Arrange
            String workspaceId = UUID.randomUUID().toString();
            String name = "Backend";
            String description = "Backend snippets";
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, name, description);

            // Act
            CategoryResponse response = useCase.execute(command);

            // Assert
            verify(categoryCommandPort).save(categoryCaptor.capture());
            Category savedCategory = categoryCaptor.getValue();

            assertThat(savedCategory.getWorkspaceId().value().toString()).isEqualTo(workspaceId);
            assertThat(savedCategory.getName()).isEqualTo(name);
            assertThat(savedCategory.getDescription()).isEqualTo(description);
            assertThat(savedCategory.getDeletedAt()).isNull();

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(savedCategory.getId().value().toString());
            assertThat(response.workspaceId()).isEqualTo(workspaceId);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.description()).isEqualTo(description);
            assertThat(response.createdAt()).isEqualTo(savedCategory.getCreatedAt());
            assertThat(response.updatedAt()).isEqualTo(savedCategory.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("when name is invalid")
    class WhenNameIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException when name is null")
        void shouldThrowIllegalArgumentExceptionWhenNameIsNull() {
            // Arrange
            String workspaceId = UUID.randomUUID().toString();
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, null, "desc");

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name is mandatory.");

            verifyNoInteractions(categoryCommandPort);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name is blank")
        void shouldThrowIllegalArgumentExceptionWhenNameIsBlank() {
            // Arrange
            String workspaceId = UUID.randomUUID().toString();
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, "   ", "desc");

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name is mandatory.");

            verifyNoInteractions(categoryCommandPort);
        }
    }

    @Nested
    @DisplayName("when workspaceId is invalid")
    class WhenWorkspaceIdIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException when workspaceId is null")
        void shouldThrowIllegalArgumentExceptionWhenWorkspaceIdIsNull() {
            // Arrange
            CreateCategoryCommand command = new CreateCategoryCommand(null, "name", "desc");

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(categoryCommandPort);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when workspaceId is invalid UUID")
        void shouldThrowIllegalArgumentExceptionWhenWorkspaceIdIsInvalidUuid() {
            // Arrange
            CreateCategoryCommand command = new CreateCategoryCommand("invalid-uuid", "name", "desc");

            // Act & Assert
            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }
}

package br.com.codexia.snippet.application.usecase.category;

import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import br.com.codexia.snippet.application.dto.command.CreateCategoryCommand;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import br.com.codexia.snippet.domain.exception.category.CategoryMaxDepthExceededException;
import br.com.codexia.snippet.domain.exception.category.DuplicateCategoryNameException;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCategoryUseCase")
class CreateCategoryUseCaseImplTest {

    @Mock
    private CategoryCommandPort categoryCommandPort;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    private CreateCategoryUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @BeforeEach
    void setUp() {
        useCase = new CreateCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Nested
    @DisplayName("when creating a root category")
    class WhenCreatingRootCategory {

        @Test
        @DisplayName("should create category with depth 0 when parentId is null")
        void shouldCreateRootCategoryWhenParentIdIsNull() {
            String workspaceId = UUID.randomUUID().toString();
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, "Backend", "Backend snippets", null);
            when(categoryQueryPort.existsByNameAndWorkspace(any(), any())).thenReturn(false);

            CategoryResponse response = useCase.execute(command);

            verify(categoryCommandPort).save(categoryCaptor.capture());
            Category saved = categoryCaptor.getValue();

            assertThat(saved.getParentId()).isNull();
            assertThat(saved.getDepth()).isZero();
            assertThat(response).isNotNull();
            assertThat(response.parentId()).isNull();
            assertThat(response.depth()).isZero();
        }

        @Test
        @DisplayName("should create category successfully")
        void shouldCreateCategorySuccessfully() {
            String workspaceId = UUID.randomUUID().toString();
            String name = "Backend";
            String description = "Backend snippets";
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, name, description, null);
            when(categoryQueryPort.existsByNameAndWorkspace(any(), any())).thenReturn(false);

            CategoryResponse response = useCase.execute(command);

            verify(categoryCommandPort).save(categoryCaptor.capture());
            Category savedCategory = categoryCaptor.getValue();

            assertThat(savedCategory.getWorkspaceId().value().toString()).isEqualTo(workspaceId);
            assertThat(savedCategory.getName()).isEqualTo(name);
            assertThat(savedCategory.getDescription()).isEqualTo(description);
            assertThat(savedCategory.getDeletedAt()).isNull();

            assertThat(response.id()).isEqualTo(savedCategory.getId().value().toString());
            assertThat(response.workspaceId()).isEqualTo(workspaceId);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.description()).isEqualTo(description);
            assertThat(response.createdAt()).isEqualTo(savedCategory.getCreatedAt());
            assertThat(response.updatedAt()).isEqualTo(savedCategory.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("when creating a child category")
    class WhenCreatingChildCategory {

        @Test
        @DisplayName("should create child with correct depth when parent exists")
        void shouldCreateChildCategoryWithCorrectDepth() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId parentId = CategoryId.generate();
            Category parent = buildCategory(parentId, workspaceId, 2);

            CreateCategoryCommand command = new CreateCategoryCommand(
                    workspaceId, "Spring", "Spring snippets", parentId.value().toString());

            when(categoryFinder.findActiveOrThrow(eq(parentId), any())).thenReturn(parent);
            when(categoryQueryPort.existsByNameAndWorkspace(any(), any())).thenReturn(false);

            useCase.execute(command);

            verify(categoryCommandPort).save(categoryCaptor.capture());
            assertThat(categoryCaptor.getValue().getDepth()).isEqualTo(3);
            assertThat(categoryCaptor.getValue().getParentId()).isEqualTo(parentId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when parent does not exist")
        void shouldThrowWhenParentNotFound() {
            String workspaceId = UUID.randomUUID().toString();
            String parentId = UUID.randomUUID().toString();
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, "Spring", null, parentId);

            when(categoryFinder.findActiveOrThrow(any(), any()))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(categoryCommandPort);
        }

        @Test
        @DisplayName("should throw CategoryMaxDepthExceededException when parent is at max depth")
        void shouldThrowWhenMaxDepthExceeded() {
            String workspaceId = UUID.randomUUID().toString();
            CategoryId parentId = CategoryId.generate();
            Category parent = buildCategory(parentId, workspaceId, 5);

            CreateCategoryCommand command = new CreateCategoryCommand(
                    workspaceId, "TooDeep", null, parentId.value().toString());

            when(categoryFinder.findActiveOrThrow(any(), any())).thenReturn(parent);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(CategoryMaxDepthExceededException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }

    @Nested
    @DisplayName("when name is duplicate")
    class WhenNameIsDuplicate {

        @Test
        @DisplayName("should throw DuplicateCategoryNameException")
        void shouldThrowWhenDuplicateName() {
            String workspaceId = UUID.randomUUID().toString();
            CreateCategoryCommand command = new CreateCategoryCommand(workspaceId, "Backend", null, null);
            when(categoryQueryPort.existsByNameAndWorkspace(any(), any())).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(DuplicateCategoryNameException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }

    @Nested
    @DisplayName("when name is invalid")
    class WhenNameIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException when name is null")
        void shouldThrowIllegalArgumentExceptionWhenNameIsNull() {
            CreateCategoryCommand command = new CreateCategoryCommand(UUID.randomUUID().toString(), null, "desc", null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name is mandatory.");

            verifyNoInteractions(categoryCommandPort);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name is blank")
        void shouldThrowIllegalArgumentExceptionWhenNameIsBlank() {
            CreateCategoryCommand command = new CreateCategoryCommand(UUID.randomUUID().toString(), "   ", "desc", null);

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
            CreateCategoryCommand command = new CreateCategoryCommand(null, "name", "desc", null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(categoryCommandPort);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when workspaceId is invalid UUID")
        void shouldThrowIllegalArgumentExceptionWhenWorkspaceIdIsInvalidUuid() {
            CreateCategoryCommand command = new CreateCategoryCommand("invalid-uuid", "name", "desc", null);

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(categoryCommandPort);
        }
    }

    private Category buildCategory(CategoryId id, String workspaceId, int depth) {
        return new Category(
                id,
                br.com.codexia.shared.domain.model.WorkspaceId.fromString(workspaceId),
                "Parent",
                null,
                null,
                depth,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}

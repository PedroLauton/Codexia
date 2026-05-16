package br.com.codexia.snippet.infrastructure.adapters.output.persistence;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryQueryAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private CategoryCommandPort categoryCommandAdapter;
    @Autowired private CategoryQueryPort categoryQueryAdapter;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("shouldReturnCategoryWhenActiveAndInWorkspace")
        void shouldReturnCategoryWhenActiveAndInWorkspace() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            var found = categoryQueryAdapter.findById(cat.getId(), ws);

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(cat.getId());
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenCategoryBelongsToAnotherWorkspace")
        void shouldReturnEmptyWhenCategoryBelongsToAnotherWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws1, "Backend", null, 0);

            assertThat(categoryQueryAdapter.findById(cat.getId(), ws2)).isEmpty();
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenCategorySoftDeleted")
        void shouldReturnEmptyWhenCategorySoftDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            cat.delete();
            categoryCommandAdapter.save(cat);

            assertThat(categoryQueryAdapter.findById(cat.getId(), ws)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDeletedById")
    class FindDeletedById {

        @Test
        @DisplayName("shouldReturnDeletedCategory")
        void shouldReturnDeletedCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            cat.delete();
            categoryCommandAdapter.save(cat);

            assertThat(categoryQueryAdapter.findDeletedById(cat.getId(), ws)).isPresent();
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenCategoryIsActive")
        void shouldReturnEmptyWhenCategoryIsActive() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            assertThat(categoryQueryAdapter.findDeletedById(cat.getId(), ws)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByNameAndWorkspace (single workspace)")
    class ExistsByNameAndWorkspaceSingle {

        @Test
        @DisplayName("shouldReturnTrueWhenNameExistsInWorkspace")
        void shouldReturnTrueWhenNameExistsInWorkspace() {
            WorkspaceId ws = WorkspaceId.generate();
            buildAndSaveCategory(ws, "Backend", null, 0);

            assertThat(categoryQueryAdapter.existsByNameAndWorkspace("Backend", ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenNameExistsInDifferentWorkspace")
        void shouldReturnFalseWhenNameExistsInDifferentWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            buildAndSaveCategory(ws1, "Backend", null, 0);

            assertThat(categoryQueryAdapter.existsByNameAndWorkspace("Backend", ws2)).isFalse();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenCategoryIsDeleted")
        void shouldReturnFalseWhenCategoryIsDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            cat.delete();
            categoryCommandAdapter.save(cat);

            assertThat(categoryQueryAdapter.existsByNameAndWorkspace("Backend", ws)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByNameAndWorkspace (exclude self)")
    class ExistsByNameAndWorkspaceExcludeSelf {

        @Test
        @DisplayName("shouldReturnFalseWhenOnlyMatchIsTheSameCategory")
        void shouldReturnFalseWhenOnlyMatchIsTheSameCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            assertThat(categoryQueryAdapter.existsByNameAndWorkspace("Backend", ws, cat.getId())).isFalse();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenAnotherCategoryHasSameName")
        void shouldReturnTrueWhenAnotherCategoryHasSameName() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat1 = buildAndSaveCategory(ws, "Backend", null, 0);
            // ddl-auto=create-drop does not create the partial unique index,
            // so saving two active categories with the same name is allowed in tests.
            Category cat2 = new Category(ws, "Backend", null, null, 0);
            categoryCommandAdapter.save(cat2);

            assertThat(categoryQueryAdapter.existsByNameAndWorkspace("Backend", ws, cat1.getId())).isTrue();
        }
    }

    @Nested
    @DisplayName("isAncestorOf — WITH RECURSIVE")
    class IsAncestorOf {

        @Test
        @DisplayName("shouldReturnTrueWhenDirectParent")
        void shouldReturnTrueWhenDirectParent() {
            WorkspaceId ws = WorkspaceId.generate();
            Category a = buildAndSaveCategory(ws, "A", null, 0);
            Category b = buildAndSaveCategory(ws, "B", a.getId(), 1);
            entityManager.flush();

            assertThat(categoryQueryAdapter.isAncestorOf(a.getId(), b.getId(), ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenIndirectAncestor")
        void shouldReturnTrueWhenIndirectAncestor() {
            WorkspaceId ws = WorkspaceId.generate();
            Category a = buildAndSaveCategory(ws, "A", null, 0);
            Category b = buildAndSaveCategory(ws, "B", a.getId(), 1);
            Category c = buildAndSaveCategory(ws, "C", b.getId(), 2);
            entityManager.flush();

            assertThat(categoryQueryAdapter.isAncestorOf(a.getId(), c.getId(), ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenNotAncestor")
        void shouldReturnFalseWhenNotAncestor() {
            WorkspaceId ws = WorkspaceId.generate();
            Category a = buildAndSaveCategory(ws, "A", null, 0);
            Category b = buildAndSaveCategory(ws, "B", a.getId(), 1);
            Category c = buildAndSaveCategory(ws, "C", null, 0);
            entityManager.flush();

            assertThat(categoryQueryAdapter.isAncestorOf(c.getId(), b.getId(), ws)).isFalse();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenSameCategory")
        void shouldReturnFalseWhenSameCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Category a = buildAndSaveCategory(ws, "A", null, 0);
            entityManager.flush();

            assertThat(categoryQueryAdapter.isAncestorOf(a.getId(), a.getId(), ws)).isFalse();
        }

        @Test
        @DisplayName("shouldNotCrossWorkspaceBoundary")
        void shouldNotCrossWorkspaceBoundary() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            Category a = buildAndSaveCategory(ws1, "A", null, 0);
            Category b = buildAndSaveCategory(ws1, "B", a.getId(), 1);
            entityManager.flush();

            assertThat(categoryQueryAdapter.isAncestorOf(a.getId(), b.getId(), ws2)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasChildren")
    class HasChildren {

        @Test
        @DisplayName("shouldReturnTrueWhenCategoryHasActiveChildren")
        void shouldReturnTrueWhenCategoryHasActiveChildren() {
            WorkspaceId ws = WorkspaceId.generate();
            Category parent = buildAndSaveCategory(ws, "Parent", null, 0);
            buildAndSaveCategory(ws, "Child", parent.getId(), 1);
            entityManager.flush();

            assertThat(categoryQueryAdapter.hasChildren(parent.getId(), ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenCategoryHasSoftDeletedChildren")
        void shouldReturnTrueWhenCategoryHasSoftDeletedChildren() {
            WorkspaceId ws = WorkspaceId.generate();
            Category parent = buildAndSaveCategory(ws, "Parent", null, 0);
            Category child = buildAndSaveCategory(ws, "Child", parent.getId(), 1);

            child.delete();
            categoryCommandAdapter.save(child);
            entityManager.flush();

            // native query counts all children including soft-deleted
            assertThat(categoryQueryAdapter.hasChildren(parent.getId(), ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenCategoryHasNoChildren")
        void shouldReturnFalseWhenCategoryHasNoChildren() {
            WorkspaceId ws = WorkspaceId.generate();
            Category parent = buildAndSaveCategory(ws, "Leaf", null, 0);
            entityManager.flush();

            assertThat(categoryQueryAdapter.hasChildren(parent.getId(), ws)).isFalse();
        }
    }

    private Category buildAndSaveCategory(WorkspaceId workspaceId, String name, CategoryId parentId, int depth) {
        Category category = new Category(workspaceId, name, null, parentId, depth);
        categoryCommandAdapter.save(category);
        return category;
    }
}

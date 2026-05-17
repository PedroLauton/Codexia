package br.com.codexia.snippet.infrastructure.adapters.output.persistence;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryCommandAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private CategoryCommandPort categoryCommandAdapter;
    @Autowired private CategoryQueryPort categoryQueryAdapter;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("when saving a root category")
    class WhenSavingARootCategory {

        @Test
        @DisplayName("shouldPersistRootCategoryWithDepthZeroAndNullParent")
        void shouldPersistRootCategoryWithDepthZeroAndNullParent() {
            WorkspaceId ws = WorkspaceId.generate();
            Category root = new Category(ws, "Backend", null, null, 0);

            categoryCommandAdapter.save(root);

            var found = categoryQueryAdapter.findById(root.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getDepth()).isEqualTo(0);
            assertThat(found.get().getParentId()).isNull();
        }
    }

    @Nested
    @DisplayName("when saving a child category")
    class WhenSavingAChildCategory {

        @Test
        @DisplayName("shouldPersistChildWithCorrectDepthAndParentReference")
        void shouldPersistChildWithCorrectDepthAndParentReference() {
            WorkspaceId ws = WorkspaceId.generate();
            Category parent = buildAndSaveCategory(ws, "Backend", null, 0);

            Category child = buildAndSaveCategory(ws, "Java", parent.getId(), 1);

            var found = categoryQueryAdapter.findById(child.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getDepth()).isEqualTo(1);
            assertThat(found.get().getParentId()).isEqualTo(parent.getId());
        }
    }

    @Nested
    @DisplayName("when soft deleting a category")
    class WhenSoftDeletingACategory {

        @Test
        @DisplayName("shouldSoftDeleteCategory")
        void shouldSoftDeleteCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend", null, 0);

            cat.delete();
            categoryCommandAdapter.save(cat);

            assertThat(categoryQueryAdapter.findById(cat.getId(), ws)).isEmpty();
            assertThat(categoryQueryAdapter.findDeletedById(cat.getId(), ws)).isPresent();
        }
    }

    @Nested
    @DisplayName("when updating subtree depth")
    class WhenUpdatingSubtreeDepth {

        @Test
        @DisplayName("shouldUpdateDepthOfAllDescendantsRecursively")
        void shouldUpdateDepthOfAllDescendantsRecursively() {
            WorkspaceId ws = WorkspaceId.generate();
            Category root = buildAndSaveCategory(ws, "Root", null, 0);
            Category child = buildAndSaveCategory(ws, "Child", root.getId(), 1);
            Category grandchild = buildAndSaveCategory(ws, "Grandchild", child.getId(), 2);

            entityManager.flush();
            categoryCommandAdapter.updateSubtreeDepth(root.getId(), 5, ws);
            entityManager.flush();
            entityManager.clear();

            var updatedChild = categoryQueryAdapter.findById(child.getId(), ws);
            var updatedGrandchild = categoryQueryAdapter.findById(grandchild.getId(), ws);

            assertThat(updatedChild).isPresent();
            assertThat(updatedChild.get().getDepth()).isEqualTo(6);
            assertThat(updatedGrandchild).isPresent();
            assertThat(updatedGrandchild.get().getDepth()).isEqualTo(7);
        }
    }

    private Category buildAndSaveCategory(WorkspaceId workspaceId, String name, CategoryId parentId, int depth) {
        Category category = new Category(workspaceId, name, null, parentId, depth);
        categoryCommandAdapter.save(category);
        return category;
    }
}

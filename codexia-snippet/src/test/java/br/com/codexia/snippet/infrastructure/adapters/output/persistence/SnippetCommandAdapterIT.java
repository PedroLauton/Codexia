package br.com.codexia.snippet.infrastructure.adapters.output.persistence;

import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.Language;
import br.com.codexia.snippet.domain.model.Snippet;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SnippetCommandAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private SnippetCommandPort snippetCommandAdapter;
    @Autowired private SnippetQueryPort snippetQueryAdapter;
    @Autowired private CategoryCommandPort categoryCommandPort;
    @Autowired private TagCommandPort tagCommandPort;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("when saving a snippet")
    class WhenSavingASnippet {

        @Test
        @DisplayName("shouldPersistSnippetWithCategoryAndTags")
        void shouldPersistSnippetWithCategoryAndTags() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend");
            Tag tag1 = buildAndSaveTag(ws, "java");
            Tag tag2 = buildAndSaveTag(ws, "spring");
            entityManager.flush();

            Snippet snippet = buildAndSaveSnippet(ws, cat.getId(), Set.of(tag1.getId(), tag2.getId()));
            entityManager.flush();
            entityManager.clear();

            var found = snippetQueryAdapter.findById(snippet.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getCategoryId()).isEqualTo(cat.getId());
            assertThat(found.get().getTagIds())
                    .containsExactlyInAnyOrder(tag1.getId(), tag2.getId());
            assertThat(found.get().getVersions()).hasSize(1);
        }

        @Test
        @DisplayName("shouldPersistSnippetWithNullCategory")
        void shouldPersistSnippetWithNullCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Snippet snippet = buildAndSaveSnippet(ws, null, Set.of());
            entityManager.flush();
            entityManager.clear();

            var found = snippetQueryAdapter.findById(snippet.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getCategoryId()).isNull();
        }

        @Test
        @DisplayName("shouldPersistSnippetWithoutTags")
        void shouldPersistSnippetWithoutTags() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend");
            entityManager.flush();

            Snippet snippet = buildAndSaveSnippet(ws, cat.getId(), Set.of());
            entityManager.flush();
            entityManager.clear();

            var found = snippetQueryAdapter.findById(snippet.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getTagIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("when soft deleting a snippet")
    class WhenSoftDeletingASnippet {

        @Test
        @DisplayName("shouldSoftDeleteSnippet")
        void shouldSoftDeleteSnippet() {
            WorkspaceId ws = WorkspaceId.generate();
            Snippet snippet = buildAndSaveSnippet(ws, null, Set.of());

            snippet.delete();
            snippetCommandAdapter.save(snippet);

            assertThat(snippetQueryAdapter.findById(snippet.getId(), ws)).isEmpty();
        }
    }

    private Category buildAndSaveCategory(WorkspaceId workspaceId, String name) {
        Category cat = new Category(workspaceId, name, null, null, 0);
        categoryCommandPort.save(cat);
        return cat;
    }

    private Tag buildAndSaveTag(WorkspaceId workspaceId, String title) {
        Tag tag = new Tag(workspaceId, title, "#10B981");
        tagCommandPort.save(tag);
        return tag;
    }

    private Snippet buildAndSaveSnippet(WorkspaceId workspaceId, CategoryId categoryId, Set<TagId> tagIds) {
        Snippet snippet = new Snippet(workspaceId, AccountId.generate(), categoryId, tagIds,
                "Hello World", "A simple snippet", "System.out.println(\"Hello\");", Language.JAVA);
        snippetCommandAdapter.save(snippet);
        return snippet;
    }
}

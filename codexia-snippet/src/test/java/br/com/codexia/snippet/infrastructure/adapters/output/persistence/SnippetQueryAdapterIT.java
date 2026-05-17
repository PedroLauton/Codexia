package br.com.codexia.snippet.infrastructure.adapters.output.persistence;

import br.com.codexia.shared.domain.model.AccountId;
import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.domain.model.aggregate.Category;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;
import br.com.codexia.snippet.domain.model.enums.Language;
import br.com.codexia.snippet.domain.model.aggregate.Snippet;
import br.com.codexia.snippet.domain.model.valueobject.TagId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SnippetQueryAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private SnippetCommandPort snippetCommandAdapter;
    @Autowired private SnippetQueryPort snippetQueryAdapter;
    @Autowired private CategoryCommandPort categoryCommandPort;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("shouldReturnSnippetWhenActiveAndInWorkspace")
        void shouldReturnSnippetWhenActiveAndInWorkspace() {
            WorkspaceId ws = WorkspaceId.generate();
            Snippet snippet = buildAndSaveSnippet(ws, null);

            var found = snippetQueryAdapter.findById(snippet.getId(), ws);

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(snippet.getId());
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenSnippetBelongsToAnotherWorkspace")
        void shouldReturnEmptyWhenSnippetBelongsToAnotherWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            Snippet snippet = buildAndSaveSnippet(ws1, null);

            assertThat(snippetQueryAdapter.findById(snippet.getId(), ws2)).isEmpty();
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenSnippetSoftDeleted")
        void shouldReturnEmptyWhenSnippetSoftDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Snippet snippet = buildAndSaveSnippet(ws, null);

            snippet.delete();
            snippetCommandAdapter.save(snippet);

            assertThat(snippetQueryAdapter.findById(snippet.getId(), ws)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsActiveSnippetsByCategoryId")
    class ExistsActiveSnippetsByCategoryId {

        @Test
        @DisplayName("shouldReturnTrueWhenActiveSnippetExistsInCategory")
        void shouldReturnTrueWhenActiveSnippetExistsInCategory() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend");
            entityManager.flush();
            buildAndSaveSnippet(ws, cat.getId());

            assertThat(snippetQueryAdapter.existsActiveSnippetsByCategoryId(cat.getId(), ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenSnippetIsSoftDeleted")
        void shouldReturnFalseWhenSnippetIsSoftDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Category cat = buildAndSaveCategory(ws, "Backend");
            entityManager.flush();
            Snippet snippet = buildAndSaveSnippet(ws, cat.getId());

            snippet.delete();
            snippetCommandAdapter.save(snippet);

            assertThat(snippetQueryAdapter.existsActiveSnippetsByCategoryId(cat.getId(), ws)).isFalse();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenNoCategoryMatch")
        void shouldReturnFalseWhenNoCategoryMatch() {
            WorkspaceId ws = WorkspaceId.generate();
            CategoryId nonExistentCategoryId = CategoryId.generate();

            assertThat(snippetQueryAdapter.existsActiveSnippetsByCategoryId(nonExistentCategoryId, ws)).isFalse();
        }
    }

    private Category buildAndSaveCategory(WorkspaceId workspaceId, String name) {
        Category cat = new Category(workspaceId, name, null, null, 0);
        categoryCommandPort.save(cat);
        return cat;
    }

    private Snippet buildAndSaveSnippet(WorkspaceId workspaceId, CategoryId categoryId) {
        Snippet snippet = new Snippet(workspaceId, AccountId.generate(), categoryId, Set.<TagId>of(),
                "Hello World", "A simple snippet", "System.out.println(\"Hello\");", Language.JAVA);
        snippetCommandAdapter.save(snippet);
        return snippet;
    }
}

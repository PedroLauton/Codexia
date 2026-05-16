package br.com.codexia.snippet.infrastructure.adapters.output.persistence;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.domain.model.Tag;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class TagCommandAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private TagCommandPort tagCommandAdapter;
    @Autowired private TagQueryPort tagQueryAdapter;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("when saving a tag")
    class WhenSavingATag {

        @Test
        @DisplayName("shouldPersistTag")
        void shouldPersistTag() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = new Tag(ws, "Java", "#3B82F6");

            tagCommandAdapter.save(tag);

            var found = tagQueryAdapter.findById(tag.getId(), ws);
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("java");
            assertThat(found.get().getHexColor()).isEqualTo("#3B82F6");
            assertThat(found.get().getWorkspaceId()).isEqualTo(ws);
        }
    }

    @Nested
    @DisplayName("when soft deleting a tag")
    class WhenSoftDeletingATag {

        @Test
        @DisplayName("shouldSoftDeleteTag")
        void shouldSoftDeleteTag() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "backend");

            tag.delete();
            tagCommandAdapter.save(tag);

            assertThat(tagQueryAdapter.findById(tag.getId(), ws)).isEmpty();
            assertThat(tagQueryAdapter.findDeletedById(tag.getId(), ws)).isPresent();
        }
    }

    @Nested
    @DisplayName("when hard deleting a tag")
    class WhenHardDeletingATag {

        @Test
        @DisplayName("shouldHardDeleteTag")
        void shouldHardDeleteTag() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "frontend");

            tag.delete();
            tagCommandAdapter.save(tag);
            entityManager.flush();

            tagCommandAdapter.delete(tag.getId());
            entityManager.flush();

            assertThat(tagQueryAdapter.findDeletedById(tag.getId(), ws)).isEmpty();
        }
    }

    private Tag buildAndSaveTag(WorkspaceId workspaceId, String title) {
        Tag tag = new Tag(workspaceId, title, "#10B981");
        tagCommandAdapter.save(tag);
        return tag;
    }
}

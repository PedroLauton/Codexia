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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TagQueryAdapterIT extends BasePersistenceIntegrationTest {

    @Autowired private TagCommandPort tagCommandAdapter;
    @Autowired private TagQueryPort tagQueryAdapter;
    @Autowired private EntityManager entityManager;

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("shouldReturnTagWhenActiveAndInWorkspace")
        void shouldReturnTagWhenActiveAndInWorkspace() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            var found = tagQueryAdapter.findById(tag.getId(), ws);

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(tag.getId());
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenTagBelongsToAnotherWorkspace")
        void shouldReturnEmptyWhenTagBelongsToAnotherWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws1, "java");

            assertThat(tagQueryAdapter.findById(tag.getId(), ws2)).isEmpty();
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenTagSoftDeleted")
        void shouldReturnEmptyWhenTagSoftDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            tag.delete();
            tagCommandAdapter.save(tag);

            assertThat(tagQueryAdapter.findById(tag.getId(), ws)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDeletedById")
    class FindDeletedById {

        @Test
        @DisplayName("shouldReturnDeletedTag")
        void shouldReturnDeletedTag() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            tag.delete();
            tagCommandAdapter.save(tag);

            assertThat(tagQueryAdapter.findDeletedById(tag.getId(), ws)).isPresent();
        }

        @Test
        @DisplayName("shouldReturnEmptyWhenTagIsActive")
        void shouldReturnEmptyWhenTagIsActive() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            assertThat(tagQueryAdapter.findDeletedById(tag.getId(), ws)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByTitleAndWorkspace (single workspace)")
    class ExistsByTitleAndWorkspaceSingle {

        @Test
        @DisplayName("shouldReturnTrueWhenTitleExistsNormalized")
        void shouldReturnTrueWhenTitleExistsNormalized() {
            WorkspaceId ws = WorkspaceId.generate();
            buildAndSaveTag(ws, "JAVA"); // stored as "java"

            assertThat(tagQueryAdapter.existsByTitleAndWorkspace("java", ws)).isTrue();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenTitleExistsInDifferentWorkspace")
        void shouldReturnFalseWhenTitleExistsInDifferentWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            buildAndSaveTag(ws1, "java");

            assertThat(tagQueryAdapter.existsByTitleAndWorkspace("java", ws2)).isFalse();
        }

        @Test
        @DisplayName("shouldReturnFalseWhenTagIsSoftDeleted")
        void shouldReturnFalseWhenTagIsSoftDeleted() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            tag.delete();
            tagCommandAdapter.save(tag);

            assertThat(tagQueryAdapter.existsByTitleAndWorkspace("java", ws)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTitleAndWorkspace (exclude self)")
    class ExistsByTitleAndWorkspaceExcludeSelf {

        @Test
        @DisplayName("shouldReturnFalseWhenOnlyMatchIsTheSameTag")
        void shouldReturnFalseWhenOnlyMatchIsTheSameTag() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws, "java");

            assertThat(tagQueryAdapter.existsByTitleAndWorkspace("java", ws, tag.getId())).isFalse();
        }

        @Test
        @DisplayName("shouldReturnTrueWhenAnotherTagHasSameTitle")
        void shouldReturnTrueWhenAnotherTagHasSameTitle() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag1 = buildAndSaveTag(ws, "java");
            // ddl-auto=create-drop does not create the partial unique index,
            // so two active tags with the same title are allowed in tests.
            Tag tag2 = new Tag(ws, "java", "#FF0000");
            tagCommandAdapter.save(tag2);

            assertThat(tagQueryAdapter.existsByTitleAndWorkspace("java", ws, tag2.getId())).isTrue();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIds {

        @Test
        @DisplayName("shouldReturnAllTagsWhenAllIdsExist")
        void shouldReturnAllTagsWhenAllIdsExist() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag1 = buildAndSaveTag(ws, "java");
            Tag tag2 = buildAndSaveTag(ws, "spring");

            List<Tag> result = tagQueryAdapter.findAllByIds(Set.of(tag1.getId(), tag2.getId()), ws);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Tag::getId)
                    .containsExactlyInAnyOrder(tag1.getId(), tag2.getId());
        }

        @Test
        @DisplayName("shouldReturnOnlyFoundTagsWhenSomeIdsAreMissing")
        void shouldReturnOnlyFoundTagsWhenSomeIdsAreMissing() {
            WorkspaceId ws = WorkspaceId.generate();
            Tag tag1 = buildAndSaveTag(ws, "java");
            var missingId = new br.com.codexia.snippet.domain.model.TagId(java.util.UUID.randomUUID());

            List<Tag> result = tagQueryAdapter.findAllByIds(Set.of(tag1.getId(), missingId), ws);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(tag1.getId());
        }

        @Test
        @DisplayName("shouldNotReturnTagsFromAnotherWorkspace")
        void shouldNotReturnTagsFromAnotherWorkspace() {
            WorkspaceId ws1 = WorkspaceId.generate();
            WorkspaceId ws2 = WorkspaceId.generate();
            Tag tag = buildAndSaveTag(ws1, "java");

            List<Tag> result = tagQueryAdapter.findAllByIds(Set.of(tag.getId()), ws2);

            assertThat(result).isEmpty();
        }
    }

    private Tag buildAndSaveTag(WorkspaceId workspaceId, String title) {
        Tag tag = new Tag(workspaceId, title, "#10B981");
        tagCommandAdapter.save(tag);
        return tag;
    }
}

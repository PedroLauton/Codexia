package br.com.codexia.snippet.application.ports.output.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.domain.model.Tag;
import br.com.codexia.snippet.domain.model.TagId;

import java.util.List;
import java.util.Set;

public interface TagQueryPort {
    List<Tag> findAllByIds(Set<TagId> ids, WorkspaceId workspaceId);

}

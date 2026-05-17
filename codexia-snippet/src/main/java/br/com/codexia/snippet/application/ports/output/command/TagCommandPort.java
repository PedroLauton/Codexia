package br.com.codexia.snippet.application.ports.output.command;

import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;

public interface TagCommandPort {
    void save(Tag tag);
    void delete(TagId id);
}

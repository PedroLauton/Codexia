package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.command;

import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.domain.model.aggregate.Tag;
import br.com.codexia.snippet.domain.model.valueobject.TagId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.TagJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.TagJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class TagCommandAdapter implements TagCommandPort {

    private final TagJpaRepository tagJpaRepository;

    public TagCommandAdapter(TagJpaRepository tagJpaRepository) {
        this.tagJpaRepository = tagJpaRepository;
    }

    @Override
    public void save(Tag tag) {
        tagJpaRepository.save(TagJpaMapper.toEntity(tag));
    }

    @Override
    public void delete(TagId tagId) {
        tagJpaRepository.deleteById(tagId.value());
    }
}

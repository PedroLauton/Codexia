package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.CategoryJpaRepository;

import org.springframework.stereotype.Component;

@Component
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryQueryAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public boolean existsById(CategoryId id, WorkspaceId workspaceId) {
        return categoryJpaRepository.existsByIdAndWorkspaceId(id.value(), workspaceId.value());
    }
}

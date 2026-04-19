package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.CategoryRepositoryPort;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.CategoryJpaRepository;

import org.springframework.stereotype.Component;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryRepositoryAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public boolean existsById(CategoryId id, WorkspaceId workspaceId) {
        return categoryJpaRepository.existsByIdAndWorkspaceId(id.value(), workspaceId.value());
    }
}

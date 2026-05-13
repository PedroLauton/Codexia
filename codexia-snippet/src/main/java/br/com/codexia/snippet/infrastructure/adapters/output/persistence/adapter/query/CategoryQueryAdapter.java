package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.query;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.CategoryJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.CategoryJpaRepository;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryQueryAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Optional<Category> findById(CategoryId id, WorkspaceId workspaceId) {
        return categoryJpaRepository.findByIdAndWorkspaceId(id.value(), workspaceId.value())
                .map(CategoryJpaMapper::toDomain);
    }

    @Override
    public Optional<Category> findDeletedById(CategoryId id, WorkspaceId workspaceId) {
        return categoryJpaRepository.findDeletedByIdAndWorkspaceId(id.value(), workspaceId.value())
                .map(CategoryJpaMapper::toDomain);
    }

    @Override
    public boolean existsById(CategoryId id, WorkspaceId workspaceId) {
        return categoryJpaRepository.existsByIdAndWorkspaceId(id.value(), workspaceId.value());
    }

    @Override
    public boolean existsByNameAndWorkspace(String name, WorkspaceId workspaceId) {
        return categoryJpaRepository.existsByNameAndWorkspaceId(name, workspaceId.value());
    }

    @Override
    public boolean existsByNameAndWorkspace(String name, WorkspaceId workspaceId, CategoryId excludeId) {
        return categoryJpaRepository.existsByNameAndWorkspaceIdAndIdNot(name, workspaceId.value(), excludeId.value());
    }

    @Override
    public boolean isAncestorOf(CategoryId potentialAncestor, CategoryId target, WorkspaceId workspaceId) {
        return categoryJpaRepository.isAncestorOf(potentialAncestor.value(), target.value(), workspaceId.value());
    }

    @Override
    public boolean hasChildren(CategoryId categoryId, WorkspaceId workspaceId) {
        return categoryJpaRepository.existsChildrenByParentIdAndWorkspaceId(categoryId.value(), workspaceId.value());
    }
}

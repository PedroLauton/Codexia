package br.com.codexia.snippet.infrastructure.adapters.output.persistence.adapter.command;

import br.com.codexia.shared.domain.model.WorkspaceId;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.domain.model.Category;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.CategoryJpaEntity;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.mapper.CategoryJpaMapper;
import br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository.CategoryJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CategoryCommandAdapter implements CategoryCommandPort {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryCommandAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public void save(Category category) {
        CategoryJpaEntity parentEntity = null;
        if (category.getParentId() != null) {
            parentEntity = categoryJpaRepository.getReferenceById(category.getParentId().value());
        }
        categoryJpaRepository.save(CategoryJpaMapper.toEntity(category, parentEntity));
    }

    @Override
    public void delete(CategoryId categoryId) {
        categoryJpaRepository.deleteById(categoryId.value());
    }

    @Override
    public void updateSubtreeDepth(CategoryId rootId, int rootNewDepth, WorkspaceId workspaceId) {
        categoryJpaRepository.updateSubtreeDepth(rootId.value(), rootNewDepth, workspaceId.value());
    }
}

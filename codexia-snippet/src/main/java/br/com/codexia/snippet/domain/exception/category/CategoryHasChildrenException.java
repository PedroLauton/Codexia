package br.com.codexia.snippet.domain.exception.category;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class CategoryHasChildrenException extends DomainException {
    public CategoryHasChildrenException(CategoryId id) {
        super(ErrorCode.CATEGORY_HAS_CHILDREN,
                "Category [" + id.value() + "] has children and cannot be purged.");
    }
}

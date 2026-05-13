package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;

public class CategoryCircularReferenceException extends DomainException {
    public CategoryCircularReferenceException(CategoryId id, CategoryId targetParentId) {
        super(ErrorCode.CATEGORY_CIRCULAR_REFERENCE,
                "Reparenting [" + id.value() + "] under [" + targetParentId.value() + "] would create a cycle.");
    }
}

package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;

public class CategorySelfReferenceException extends DomainException {
    public CategorySelfReferenceException(CategoryId id) {
        super(ErrorCode.CATEGORY_SELF_REFERENCE,
                "Category [" + id.value() + "] cannot be its own parent.");
    }
}

package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;

public class CategoryNotDeletedException extends DomainException {

    public CategoryNotDeletedException(CategoryId categoryId) {
        super(ErrorCode.CATEGORY_NOT_DELETED,
                "Restore rejected: Category [" + categoryId.value() + "] is not deleted.");
    }
}
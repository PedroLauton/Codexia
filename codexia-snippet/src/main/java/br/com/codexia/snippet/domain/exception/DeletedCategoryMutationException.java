package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;

public class DeletedCategoryMutationException extends DomainException {

    public DeletedCategoryMutationException(CategoryId categoryId) {
        super(ErrorCode.DELETED_CATEGORY_MUTATION,
                "Change rejected: Category [" + categoryId.value() + "] is deleted.");
    }
}
package br.com.codexia.snippet.domain.exception.category;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.valueobject.CategoryId;

public class CategoryHasActiveSnippetsException extends DomainException {
    public CategoryHasActiveSnippetsException(CategoryId categoryId) {
        super(ErrorCode.CATEGORY_HAS_ACTIVE_SNIPPETS,
                "Category [" + categoryId.value() + "] has active snippets.");
    }
}
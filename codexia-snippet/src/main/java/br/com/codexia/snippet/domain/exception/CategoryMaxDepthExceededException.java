package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;

public class CategoryMaxDepthExceededException extends DomainException {
    public CategoryMaxDepthExceededException(CategoryId id, int depth) {
        super(ErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED,
                "Category [" + id.value() + "] would be at depth " + depth
                + ", exceeding max of 5.");
    }
}

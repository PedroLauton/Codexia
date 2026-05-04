package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.WorkspaceId;

public class DuplicateCategoryNameException extends DomainException {
    public DuplicateCategoryNameException(String name, WorkspaceId workspaceId) {
        super(ErrorCode.DUPLICATE_CATEGORY_NAME,
                "Category with name [" + name + "] already exists in workspace [" + workspaceId.value() + "].");
    }
}

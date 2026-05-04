package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.shared.domain.model.WorkspaceId;

public class DuplicateTagTitleException extends DomainException {

    public DuplicateTagTitleException(String title, WorkspaceId workspaceId) {
        super(ErrorCode.DUPLICATE_TAG_TITLE,
                "Tag with title [" + title + "] already exists in workspace [" + workspaceId.value() + "].");
    }
}

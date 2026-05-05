package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.TagId;

public class TagNotDeletedException extends DomainException {

    public TagNotDeletedException(TagId tagId) {
        super(ErrorCode.TAG_NOT_DELETED,
                "Restore rejected: Tag [" + tagId.value() + "] is not deleted.");
    }
}

package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.CategoryId;
import br.com.codexia.snippet.domain.model.TagId;

public class DeletedTagMutationException extends DomainException {

    public DeletedTagMutationException(TagId tagId) {
        super(ErrorCode.DELETED_TAG_MUTATION,
                "Change rejected: Tag [" + tagId.value() + "] is deleted.");
    }

    public DeletedTagMutationException() {
        super(ErrorCode.DELETED_TAG_MUTATION);
    }

}

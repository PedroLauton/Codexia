package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;

public class DeletedTagMutationException extends DomainException {

    public DeletedTagMutationException(String message) {
        super(ErrorCode.DELETED_TAG_MUTATION,  message);
    }

    public DeletedTagMutationException() {
        super(ErrorCode.DELETED_TAG_MUTATION);
    }

}

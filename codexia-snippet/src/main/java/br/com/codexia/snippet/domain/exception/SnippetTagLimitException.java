package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.SnippetId;

public class SnippetTagLimitException extends DomainException {

    public SnippetTagLimitException(String message) {
        super(ErrorCode.SNIPPET_TAG_LIMIT_EXCEEDED,  message);
    }
}

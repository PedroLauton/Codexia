package br.com.codexia.snippet.domain.exception.snippet;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;

public class SnippetTagLimitException extends DomainException {

    public SnippetTagLimitException(String message) {
        super(ErrorCode.SNIPPET_TAG_LIMIT_EXCEEDED,  message);
    }
}

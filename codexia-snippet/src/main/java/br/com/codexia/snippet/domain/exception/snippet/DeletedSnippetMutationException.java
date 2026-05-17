package br.com.codexia.snippet.domain.exception.snippet;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.domain.exception.ErrorCode;
import br.com.codexia.snippet.domain.model.valueobject.SnippetId;

public class DeletedSnippetMutationException extends DomainException {

    public DeletedSnippetMutationException(SnippetId snippetId) {
        super(ErrorCode.DELETED_SNIPPET_MUTATION, "Change rejected: Snippet [" + snippetId.value() + "] is deleted.");
    }
}

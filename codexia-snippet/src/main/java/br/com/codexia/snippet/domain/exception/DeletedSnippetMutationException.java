package br.com.codexia.snippet.domain.exception;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.snippet.domain.model.SnippetId;

public class DeletedSnippetMutationException extends DomainException {

    public DeletedSnippetMutationException(SnippetId snippetId) {
        super("Change rejected: Snippet [" + snippetId.value() + "] is deleted.");
    }
}

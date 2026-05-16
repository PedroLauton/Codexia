package br.com.codexia.snippet.infrastructure.adapters.input.rest.handler;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.infrastructure.adapters.input.rest.dto.ErrorResponse;
import br.com.codexia.shared.infrastructure.adapters.input.rest.handler.ExceptionHandlerSupport;
import br.com.codexia.snippet.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SnippetExceptionHandler {

    @ExceptionHandler({
            DuplicateCategoryNameException.class,
            DuplicateTagTitleException.class,
            CategoryHasActiveSnippetsException.class,
            CategoryHasChildrenException.class,
            CategoryCircularReferenceException.class,
            CategorySelfReferenceException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(
            DomainException ex, HttpServletRequest request) {
        return ExceptionHandlerSupport.build(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler({
            DeletedCategoryMutationException.class,
            DeletedSnippetMutationException.class,
            DeletedTagMutationException.class,
            CategoryMaxDepthExceededException.class,
            CategoryNotDeletedException.class,
            TagNotDeletedException.class
    })
    public ResponseEntity<ErrorResponse> handleUnprocessable(
            DomainException ex, HttpServletRequest request) {
        return ExceptionHandlerSupport.build(HttpStatus.UNPROCESSABLE_ENTITY, ex, request);
    }
}

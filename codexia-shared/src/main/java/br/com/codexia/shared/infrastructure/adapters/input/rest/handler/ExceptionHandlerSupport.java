package br.com.codexia.shared.infrastructure.adapters.input.rest.handler;

import br.com.codexia.shared.domain.exception.DomainException;
import br.com.codexia.shared.infrastructure.adapters.input.rest.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public final class ExceptionHandlerSupport {

    private ExceptionHandlerSupport() {}

    public static ResponseEntity<ErrorResponse> build(
            HttpStatus status, DomainException ex, HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                "https://codexia.io/errors/" + ex.getErrorCode().getCode().toLowerCase(),
                ex.getErrorCode().getDefaultMessage(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.status(status).body(body);
    }
}

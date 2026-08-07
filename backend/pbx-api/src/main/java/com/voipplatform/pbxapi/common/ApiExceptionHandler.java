package com.voipplatform.pbxapi.common;

import com.voipplatform.pbxapi.extension.ExtensionAlreadyExistsException;
import com.voipplatform.pbxapi.extension.ExtensionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates exceptions into RFC 9457 problem responses, so controllers and
 * services never handle HTTP status codes themselves.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ExtensionAlreadyExistsException.class)
    public ProblemDetail handleConflict(ExtensionAlreadyExistsException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        p.setTitle("Extension already exists");
        return p;
    }

    @ExceptionHandler(ExtensionNotFoundException.class)
    public ProblemDetail handleNotFound(ExtensionNotFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        p.setTitle("Extension not found");
        return p;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        ProblemDetail p = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        p.setTitle("Validation failed");
        p.setProperty("errors", errors);
        return p;
    }
}

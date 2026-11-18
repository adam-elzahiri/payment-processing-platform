package io.github.adam_elzahiri.payment_processing_platform.exception;

import io.github.adam_elzahiri.payment_processing_platform.auth.exception.InvalidCredentialsException;
import io.github.adam_elzahiri.payment_processing_platform.user.exception.EmailAlreadyInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static URI newInstanceUri() {
        return URI.create("urn:uuid:" + UUID.randomUUID());
    }

    // Framework exceptions
    // ============================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value")
                ))
                .toList();
        problem.setInstance(newInstanceUri());
        problem.setTitle("Validation failed");
        problem.setProperty("code", "REQUEST_VALIDATION_FAILED");
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    // Custom exceptions
    // ============================================

    @ExceptionHandler(InvalidCredentialsException.class)
    private ResponseEntity<ProblemDetail> handle(InvalidCredentialsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
        problem.setInstance(newInstanceUri());
        problem.setTitle("Invalid credentials");
        problem.setProperty("code", "INVALID_CREDENTIALS");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    private ResponseEntity<ProblemDetail> handle(EmailAlreadyInUseException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
        problem.setInstance(newInstanceUri());
        problem.setTitle("Email already in use");
        problem.setProperty("code", "EMAIL_ALREADY_IN_USE");

        return ResponseEntity.badRequest().body(problem);
    }

}

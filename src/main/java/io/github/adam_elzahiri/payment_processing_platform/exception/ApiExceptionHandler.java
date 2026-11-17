package io.github.adam_elzahiri.payment_processing_platform.exception;

import io.github.adam_elzahiri.payment_processing_platform.auth.exception.InvalidCredentialsException;
import io.github.adam_elzahiri.payment_processing_platform.user.exception.EmailAlreadyInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    private URI newInstanceUri() {
        return URI.create("urn:uuid:" + UUID.randomUUID());
    }

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

package io.github.adam_elzahiri.payment_processing_platform.auth.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("The username or password is invalid.");
    }

}

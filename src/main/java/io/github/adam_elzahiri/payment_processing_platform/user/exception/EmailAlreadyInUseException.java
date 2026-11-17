package io.github.adam_elzahiri.payment_processing_platform.user.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("A user with this email address already exists.");
    }

}

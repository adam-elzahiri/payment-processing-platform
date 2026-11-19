package io.github.adam_elzahiri.payment_processing_platform.infrastructure.persistence.hibernate;

import org.hibernate.exception.ConstraintViolationException;

public final class HibernateConstraintViolationDetector {

    private HibernateConstraintViolationDetector() {}

    public static boolean violatesConstraint(Throwable exception, String expectedName) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                return expectedName.equals(violation.getConstraintName());
            }
        }
        return false;
    }

}

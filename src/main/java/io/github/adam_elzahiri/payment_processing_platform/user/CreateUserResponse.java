package io.github.adam_elzahiri.payment_processing_platform.user;

import java.time.Instant;
import java.util.UUID;

public record CreateUserResponse(

        UUID id,
        String firstName,
        String lastName,
        Instant createdAt

) {}

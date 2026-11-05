package io.github.adam_elzahiri.payment_processing_platform.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(

        UUID id,
        String firstName,
        String lastName,
        Instant createdAt

) {}

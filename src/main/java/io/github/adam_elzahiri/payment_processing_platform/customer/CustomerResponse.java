package io.github.adam_elzahiri.payment_processing_platform.customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(

        UUID id,
        String firstName,
        String lastName,
        Instant createdAt

) {}

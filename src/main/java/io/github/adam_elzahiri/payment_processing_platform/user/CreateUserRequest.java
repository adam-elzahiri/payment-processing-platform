package io.github.adam_elzahiri.payment_processing_platform.user;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {}

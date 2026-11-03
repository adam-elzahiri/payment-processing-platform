package io.github.adam_elzahiri.payment_processing_platform.customer;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {}

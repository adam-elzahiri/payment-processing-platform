package io.github.adam_elzahiri.payment_processing_platform.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        @Email
        @Size(max = 320)
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {}

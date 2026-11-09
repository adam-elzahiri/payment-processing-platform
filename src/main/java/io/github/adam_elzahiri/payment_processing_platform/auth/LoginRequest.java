package io.github.adam_elzahiri.payment_processing_platform.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

    @NotBlank
    @Email
    @Size(max = 320)
    String email,

    @NotBlank
    @Size(min = 15, max = 64)
    String password

) {}
package io.github.adam_elzahiri.payment_processing_platform.auth;

public record LoginResponse (

        String accessToken,
        String tokenType,
        long expiresInSeconds

) {}
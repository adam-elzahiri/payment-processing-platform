package io.github.adam_elzahiri.payment_processing_platform.auth;

public record AccessTokenResponse(

        String accessToken,
        String tokenType,
        long expiresInSeconds

) {}
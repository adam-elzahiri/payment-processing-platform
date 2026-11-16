package io.github.adam_elzahiri.payment_processing_platform.auth;

import io.github.adam_elzahiri.payment_processing_platform.testsupport.PostgresHttpIntegrationTest;
import io.github.adam_elzahiri.payment_processing_platform.user.User;
import io.github.adam_elzahiri.payment_processing_platform.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@PostgresHttpIntegrationTest
public class LoginIT {

    private static final long EXPECTED_TOKEN_TTL_SECONDS = 15 * 60;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void loggingInWithValidCredentialsReturnsAccessToken() {
        String email = "login-" + UUID.randomUUID() + "@example.com";
        String password = "correct-horse-battery";

        User testUser = userRepository.save(new User(
                email,
                passwordEncoder.encode(password),
                "Test",
                "User"
        ));

        try {
            LoginRequest request = new LoginRequest(
                    email.toUpperCase(Locale.ROOT),
                    password
            );

            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    "/auth/login",
                    request,
                    LoginResponse.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().accessToken()).isNotBlank();
            assertThat(response.getBody().tokenType()).isEqualTo("Bearer");
            assertThat(response.getBody().expiresInSeconds()).isEqualTo(EXPECTED_TOKEN_TTL_SECONDS);

            Jwt decodedToken = jwtDecoder.decode(response.getBody().accessToken());
            assertThat(decodedToken.getSubject()).isEqualTo(testUser.getId().toString());
        } finally {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
    }
}

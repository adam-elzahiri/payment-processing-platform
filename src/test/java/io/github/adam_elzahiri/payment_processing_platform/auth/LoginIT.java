package io.github.adam_elzahiri.payment_processing_platform.auth;

import io.github.adam_elzahiri.payment_processing_platform.testsupport.PostgresHttpIntegrationTest;
import io.github.adam_elzahiri.payment_processing_platform.user.User;
import io.github.adam_elzahiri.payment_processing_platform.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    private static final String DUMMY_PASSWORD = "correct-horse-battery";

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
        final String email = newEmail("valid-credentials");
        final String password = "correct-horse-battery";

        User testUser = userRepository.save(new User(
                email,
                passwordEncoder.encode(password),
                "Test",
                "User"
        ));

        try {
            LoginRequest request = new LoginRequest(
                    email.toUpperCase(Locale.ROOT), // Uppercasing to test if email is normalized properly
                    password
            );

            ResponseEntity<LoginResponse> response = positivePost(request);

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

    @Test
    void loggingInWithIncorrectEmailReturnsUnauthorizedResponse() {
        final String email = newEmail("incorrect-used-email");
        final String invalidEmail = newEmail("incorrect-used-email");

        userRepository.save(new User(
                email,
                passwordEncoder.encode(DUMMY_PASSWORD),
                "Test",
                "User"
        ));

        try {
            LoginRequest request = new LoginRequest(
                    invalidEmail,
                    DUMMY_PASSWORD
            );

            ResponseEntity<ProblemDetail> response = negativePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

            ProblemDetail problem = response.getBody();
            assertThat(problem).isNotNull();
            assertThat(problem.getProperties())
                    .containsEntry("code", "INVALID_CREDENTIALS");
        } finally {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
    }

    @Test
    void loggingInWithIncorrectPasswordReturnsUnauthorizedResponse() {
        final String email = newEmail("incorrect-password");

        userRepository.save(new User(
                email,
                passwordEncoder.encode(DUMMY_PASSWORD),
                "Test",
                "User"
        ));

        try {
            LoginRequest request = new LoginRequest(
                    email,
                    "incorrect-password"
            );

            ResponseEntity<ProblemDetail> response = negativePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

            ProblemDetail problem = response.getBody();
            assertThat(problem).isNotNull();
            assertThat(problem.getProperties())
                    .containsEntry("code", "INVALID_CREDENTIALS");
        } finally {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
    }

    private ResponseEntity<LoginResponse> positivePost(LoginRequest request) {
        return restTemplate.postForEntity(
                "/auth/login",
                request,
                LoginResponse.class
        );
    }

    private ResponseEntity<ProblemDetail> negativePost(LoginRequest request) {
        return restTemplate.postForEntity(
                "/auth/login",
                request,
                ProblemDetail.class
        );
    }

    private static String newEmail(String context) {
        return (context + "-" + UUID.randomUUID() + "@example.com")
                .toLowerCase(Locale.ROOT);
    }

}

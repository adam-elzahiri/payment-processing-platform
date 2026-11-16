package io.github.adam_elzahiri.payment_processing_platform.user;

import io.github.adam_elzahiri.payment_processing_platform.testsupport.PostgresHttpIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@PostgresHttpIntegrationTest
public class UserRegistrationIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registeringUserCreatesUserAndReturnsCreatedResponse() {
        String email = "registration-" + UUID.randomUUID() + "@example.com";
        String password = "correct-horse-battery";

        try {
            CreateUserRequest request = new CreateUserRequest(
                    email.toUpperCase(Locale.ROOT),
                    password,
                    "Ada",
                    "Lovelace"
            );

            ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                    "/users",
                    request,
                    UserResponse.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isNotNull();
            assertThat(response.getBody().firstName()).isEqualTo("Ada");
            assertThat(response.getBody().lastName()).isEqualTo("Lovelace");
            assertThat(response.getBody().createdAt()).isNotNull();

            User savedUser = userRepository.findByEmail(email).orElseThrow();
            assertThat(savedUser.getId()).isEqualTo(response.getBody().id());
            assertThat(savedUser.getPasswordHash()).isNotEqualTo(password);
            assertThat(passwordEncoder.matches(password, savedUser.getPasswordHash())).isTrue();
        } finally {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
    }

}

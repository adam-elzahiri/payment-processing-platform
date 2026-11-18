package io.github.adam_elzahiri.payment_processing_platform.user;

import io.github.adam_elzahiri.payment_processing_platform.testsupport.PostgresHttpIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@PostgresHttpIntegrationTest
public class UserRegistrationIT {

    private static final String DUMMY_PASSWORD = "correct-horse-battery";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registeringUserCreatesUserAndReturnsCreatedResponse() {
        final String email = newEmail("registration");

        try {
            CreateUserRequest request = new CreateUserRequest(
                    email.toUpperCase(Locale.ROOT), // Uppercasing to test if email is normalized properly
                    DUMMY_PASSWORD,
                    "firstName",
                    "lastName"
            );
            ResponseEntity<UserProfileResponse> response = positivePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            User savedUser = userRepository.findByEmail(email).orElseThrow();
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getCreatedAt()).isNotNull();
            assertThat(savedUser.getFirstName()).isEqualTo("firstName");
            assertThat(savedUser.getLastName()).isEqualTo("lastName");

            assertThat(savedUser.getEmail()).isEqualTo(email); // Check if persisted email is normalized

            assertThat(savedUser.getPasswordHash()).isNotEqualTo(DUMMY_PASSWORD);
            assertThat(passwordEncoder.matches(DUMMY_PASSWORD, savedUser.getPasswordHash())).isTrue();
        } finally {
            cleanUp(email);
        }
    }

    @Test
    void registeringUserWithInvalidEmailReturnsBadResponse() {
        final String invalidEmail = "invalid-email-" + UUID.randomUUID();

        try {
            CreateUserRequest request = new CreateUserRequest(
                    invalidEmail.toUpperCase(Locale.ROOT),
                    DUMMY_PASSWORD,
                    "firstName",
                    "lastName"
            );
            ResponseEntity<ProblemDetail> response = negativePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            ProblemDetail problem = response.getBody();
            assertThat(problem).isNotNull();
            assertThat(problem.getProperties())
                    .containsEntry("code", "REQUEST_VALIDATION_FAILED");

            assertThat(userRepository.findByEmail(invalidEmail)).isEmpty();
        } finally {
            cleanUp(invalidEmail);
        }
    }

    @ParameterizedTest(name = "password length {0} is rejected")
    @ValueSource(ints = {7, 65})
    void registeringUserWithInvalidPasswordLengthReturnsBadResponse(int passwordLength) {
        final String email = newEmail("invalidPasswordLength");

        try {
            CreateUserRequest request = new CreateUserRequest(
                    email,
                    "a".repeat(passwordLength),
                    "firstNameTooShort",
                    "lastNameTooShort"
            );
            ResponseEntity<ProblemDetail> response = negativePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            ProblemDetail problem = response.getBody();
            assertThat(problem).isNotNull();
            assertThat(problem.getProperties())
                    .containsEntry("code", "REQUEST_VALIDATION_FAILED");

            assertThat(userRepository.findByEmail(email)).isEmpty();
        } finally {
            cleanUp(email);
        }
    }

    @Test
    void registeringUserWithAlreadyInUseEmailReturnsBadResponse() {
        final String emailInUse = newEmail("emailAlreadyInUse");

        final String existingUserFirstName = "existingUser";
        final String newUserFirstName = "newUser";

        try {
            User savingUser = new User(
                    emailInUse,
                    DUMMY_PASSWORD,
                    existingUserFirstName,
                    "lastName"
            );
            userRepository.save(savingUser);

            CreateUserRequest request = new CreateUserRequest(
                    emailInUse,
                    DUMMY_PASSWORD,
                    newUserFirstName,
                    "lastName"
            );
            ResponseEntity<ProblemDetail> response = negativePost(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            ProblemDetail problemTooLong = response.getBody();
            assertThat(problemTooLong).isNotNull();
            assertThat(problemTooLong.getProperties())
                    .containsEntry("code", "EMAIL_ALREADY_IN_USE");

            User existingUser = userRepository.findByEmail(emailInUse)
                    .orElseThrow(() -> new AssertionError("Original user was removed"));
            assertThat(existingUser.getFirstName()).isEqualTo(existingUserFirstName);

        } finally {
            cleanUp(emailInUse);
        }
    }

    private ResponseEntity<UserProfileResponse> positivePost(CreateUserRequest request) {
        return restTemplate.postForEntity(
                "/users",
                request,
                UserProfileResponse.class
        );
    }

    private ResponseEntity<ProblemDetail> negativePost(CreateUserRequest request) {
        return restTemplate.postForEntity(
                "/users",
                request,
                ProblemDetail.class
        );
    }

    private void cleanUp(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }

    private static String newEmail(String context) {
        return (context + "-" + UUID.randomUUID() + "@example.com")
                .toLowerCase(Locale.ROOT);
    }

}

package io.github.adam_elzahiri.payment_processing_platform.user;

import io.github.adam_elzahiri.payment_processing_platform.auth.AccessTokenResponse;
import io.github.adam_elzahiri.payment_processing_platform.auth.TokenService;
import io.github.adam_elzahiri.payment_processing_platform.infrastructure.persistence.hibernate.HibernateConstraintViolationDetector;
import io.github.adam_elzahiri.payment_processing_platform.user.exception.EmailAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String EMAIL_UNIQUE_CONSTRAINT_NAME = "app_user_email_key";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    AccessTokenResponse createUser(CreateUserRequest createUserRequest) {
        String normalizedEmail = createUserRequest.email()
                .strip()
                .toLowerCase(Locale.ROOT);

        String passwordHash = passwordEncoder.encode(createUserRequest.password());

        User newUser = new User(
                normalizedEmail,
                passwordHash,
                createUserRequest.firstName().strip(),
                createUserRequest.lastName().strip()
        );

        User savedUser;
        try {
            savedUser = userRepository.saveAndFlush(newUser);
        } catch (DataIntegrityViolationException exception) {
            if (HibernateConstraintViolationDetector
                    .violatesConstraint(exception, EMAIL_UNIQUE_CONSTRAINT_NAME)) {
                throw new EmailAlreadyInUseException();
            }
            throw exception;
        }

        return tokenService.createAccessToken(savedUser.getId());
    }

    public User getUserById(UUID  userId) {
        return userRepository.findById(userId).orElseThrow();
    }

}

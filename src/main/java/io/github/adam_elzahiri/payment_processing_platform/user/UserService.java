package io.github.adam_elzahiri.payment_processing_platform.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    User createUser(CreateUserRequest createUserRequest) {
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

        return userRepository.save(newUser);
    }

    public User getUserById(UUID  userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}

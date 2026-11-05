package io.github.adam_elzahiri.payment_processing_platform.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    User createUser(CreateUserRequest createUserRequest) {
        User newUser = new User(createUserRequest.firstName(), createUserRequest.lastName());

        return userRepository.save(newUser);
    }

}

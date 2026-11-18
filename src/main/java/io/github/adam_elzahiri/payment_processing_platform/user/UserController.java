package io.github.adam_elzahiri.payment_processing_platform.user;

import io.github.adam_elzahiri.payment_processing_platform.auth.AccessTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<AccessTokenResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        AccessTokenResponse response = userService.createUser(createUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User currentUser = userService.getUserById(userId);

        UserProfileResponse response = new UserProfileResponse(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

}

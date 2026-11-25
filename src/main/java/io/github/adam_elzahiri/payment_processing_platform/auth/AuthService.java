package io.github.adam_elzahiri.payment_processing_platform.auth;

import io.github.adam_elzahiri.payment_processing_platform.auth.exception.InvalidCredentialsException;
import io.github.adam_elzahiri.payment_processing_platform.user.User;
import io.github.adam_elzahiri.payment_processing_platform.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AccessTokenResponse login(LoginRequest request) {
        String normalizedEmail = request.email()
                .strip()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        String currentPasswordHash = user.getPasswordHash();
        if (!passwordEncoder.matches(request.password(), currentPasswordHash)) {
            throw new InvalidCredentialsException();
        }

        if (passwordEncoder.upgradeEncoding(currentPasswordHash)) {
            String upgradedPasswordHash = passwordEncoder.encode(request.password());
            userRepository.updatePasswordHashIfUnchanged(
                    user.getId(),
                    currentPasswordHash,
                    upgradedPasswordHash
            );
        }

        return tokenService.createAccessToken(user.getId());
    }

}

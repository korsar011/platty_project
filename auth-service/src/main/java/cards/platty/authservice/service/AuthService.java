package cards.platty.authservice.service;

import cards.platty.authservice.dto.AccessTokenResponse;
import cards.platty.authservice.dto.AuthResponse;
import cards.platty.authservice.dto.RefreshTokenResponse;
import cards.platty.authservice.dto.UserRegistrationResponse;
import cards.platty.authservice.entity.User;
import cards.platty.authservice.exception.UserNotFoundException;
import cards.platty.authservice.exception.UsernameAlreadyExistsException;
import cards.platty.authservice.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserCredentialRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserRegistrationResponse saveUser(User credential) {
        log.info("Checking if username '{}' is already taken", credential.getUsername());

        if (userRepository.existsByUsername(credential.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + credential.getUsername() + "' is already taken.");
        }

        log.info("Saving new user: {}", credential.getUsername());
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        User savedUser = userRepository.save(credential);

        log.info("User successfully saved with ID: {}", savedUser.getId());
        return new UserRegistrationResponse(
                "User added successfully",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public AuthResponse generateTokens(User userCredential) {
        long userId = userCredential.getId();
        log.info("Generating tokens for user ID: {}", userId);
        AccessTokenResponse accessTokenResponse = jwtService.generateAccessToken(userCredential);
        RefreshTokenResponse refreshTokenResponse = jwtService.generateRefreshToken(userCredential);

        String accessToken = accessTokenResponse.getAccessToken();
        String refreshToken = refreshTokenResponse.getRefreshToken();
        LocalDateTime accessTokenExpiry = accessTokenResponse.getExpiryDate();
        LocalDateTime refreshTokenExpiry = refreshTokenResponse.getExpiryDate();

        log.info("Tokens generated for user ID: {}", userId);
        return new AuthResponse(accessToken, refreshToken, accessTokenExpiry, refreshTokenExpiry);
    }

    public AuthResponse refreshTokens(String refreshToken) {
        log.info("Validating refresh token for token renewal");
        jwtService.validateToken(refreshToken);

        String userId = jwtService.getUserIdFromToken(refreshToken);
        User userCredential = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserNotFoundException("User id from token is invalid"));
        log.info("Refresh token validated, generating new tokens for user ID: {}", userId);

        return generateTokens(userCredential);
    }
}

package cards.platty.authservice.controller;

import cards.platty.authservice.dto.AuthRequest;
import cards.platty.authservice.dto.AuthResponse;
import cards.platty.authservice.dto.UserRegistrationResponse;
import cards.platty.authservice.entity.User;
import cards.platty.authservice.exception.InvalidCredentialsException;
import cards.platty.authservice.service.AuthService;
import cards.platty.authservice.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserCredentialService userCredentialService;

    @PostMapping("/register")
    public UserRegistrationResponse registerUser(@RequestBody AuthRequest user) {
        UserRegistrationResponse response = authService.saveUser(user.toEntity());
        return response;
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = userCredentialService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/token")
    public AuthResponse authenticateUser(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            User userCredential = userCredentialService.findByUsername(authRequest.getUsername());
            return authService.generateTokens(userCredential);
        } else {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return authService.refreshTokens(refreshToken);
    }
}

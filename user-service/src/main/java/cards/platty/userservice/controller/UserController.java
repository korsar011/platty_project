package cards.platty.userservice.controller;

import cards.platty.userservice.dto.ImageUrlDto;
import cards.platty.userservice.dto.UpdateUserDtoRq;
import cards.platty.userservice.dto.UserDto;
import cards.platty.userservice.entity.UserEntity;
import cards.platty.userservice.exception.UsernameAlreadyExistsException;
import cards.platty.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        if (userService.isUsernameTaken(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is already taken");
        }

        UserEntity user = convertToUserEntity(userDto);
        return ResponseEntity.ok(userService.createUser(user));
    }

    private UserEntity convertToUserEntity(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        return user;
    }

    @PutMapping("/{id}/profile-image")
    public ResponseEntity<Void> updateUserProfileImage(@PathVariable Long id, @RequestBody ImageUrlDto imageUrlDto) {
        userService.updateUserImageUrl(id, imageUrlDto.getImageUrl());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserEntity> updateCurrentUser(@RequestBody UpdateUserDtoRq updateUserDtoRq) {
        Long currentUserId = userService.getCurrentUserId();
        try {
            UserEntity updatedUser = userService.updateUserById(currentUserId, updateUserDtoRq);
            return ResponseEntity.ok(updatedUser);
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = !userService.isUsernameTaken(username);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser() {
        Long currentUserId = userService.getCurrentUserId();
        return ResponseEntity.ok(userService.getUserById(currentUserId));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("JWT Token is missing");
    }
}
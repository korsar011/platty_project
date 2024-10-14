package cards.platty.userservice.service;

import cards.platty.userservice.dto.UpdateUserDtoRq;
import cards.platty.userservice.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import cards.platty.userservice.exception.UserNotFoundException;
import cards.platty.userservice.exception.AccessDeniedException;
import cards.platty.userservice.exception.UsernameAlreadyExistsException;
import cards.platty.userservice.repository.UserRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public UserEntity getUserById(Long id) {
        log.info("Attempting to find user with ID: {}", id);

        Long userIdFromToken = getCurrentUserId();

        if (!id.equals(userIdFromToken)) {
            log.error("Access denied for userId: {} to userId: {}", userIdFromToken, id);
            throw new AccessDeniedException("Access denied");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No user found with ID: {}", id);
                    return new UserNotFoundException("No user with id " + id);
                });
    }

    @Override
    @Transactional
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        log.info("Creating new user: {}", userEntity.getUsername());
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserEntity updateUserImageUrl(Long userId, String imageUrl) {
        log.info("Attempting to update image URL for user ID: {}", userId);

        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL must not be empty");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        user.setImageUrl(imageUrl);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity updateUserById(Long id, UpdateUserDtoRq updateUserDtoRq) {
        log.info("Attempting to update user with ID: {}", id);

        Long userIdFromToken = getCurrentUserId();

        if (!id.equals(userIdFromToken)) {
            log.error("Access denied for userId: {} to update userId: {}", userIdFromToken, id);
            throw new AccessDeniedException("Access denied");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found for update", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });

        if (!user.getUsername().equals(updateUserDtoRq.getUsername()) &&
                isUsernameTaken(updateUserDtoRq.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        setUserFieldsFromUpdateDto(user, updateUserDtoRq);
        return userRepository.save(user);
    }

    private void setUserFieldsFromUpdateDto(UserEntity user, UpdateUserDtoRq updateUserDtoRq) {
        user.setUsername(updateUserDtoRq.getUsername());
        user.setFirstName(updateUserDtoRq.getFirstName());
        user.setLastName(updateUserDtoRq.getLastName());
        user.setEmail(updateUserDtoRq.getEmail());
        user.setBirthday(updateUserDtoRq.getBirthday());
        user.setCountry(updateUserDtoRq.getCountry());
        user.setLanguages(updateUserDtoRq.getLanguages());
    }


    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();
        if (details instanceof Map) {
            Map<String, Object> authDetails = (Map<String, Object>) details;
            String userId = (String) authDetails.get("userId");
            return Long.valueOf(userId);
        }
        log.error("No authentication or user ID in context");
        throw new AccessDeniedException("Current user is not authenticated or user ID is missing");
    }

}

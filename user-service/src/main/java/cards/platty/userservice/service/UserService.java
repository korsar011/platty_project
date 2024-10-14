package cards.platty.userservice.service;

import cards.platty.userservice.dto.UpdateUserDtoRq;
import cards.platty.userservice.entity.UserEntity;

public interface UserService {

    UserEntity getUserById(Long id);

    UserEntity createUser(UserEntity userEntity);

    UserEntity updateUserImageUrl (Long userId, String imageUrl);

    UserEntity updateUserById(Long id, UpdateUserDtoRq updateUserDtoRq);

    public boolean isUsernameTaken(String username);

    public Long getCurrentUserId();

    }
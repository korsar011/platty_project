package cards.platty.authservice.service;

import cards.platty.authservice.entity.User;

public interface UserCredentialService {

    User findByUsername(String username);

    public boolean isUsernameAvailable(String username);

    }

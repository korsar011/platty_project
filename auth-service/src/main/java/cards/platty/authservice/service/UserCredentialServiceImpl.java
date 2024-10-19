package cards.platty.authservice.service;

import cards.platty.authservice.entity.User;
import cards.platty.authservice.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public User findByUsername(String username) {
        return userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username %s not found"
                        .formatted(username)));
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userCredentialRepository.existsByUsername(username);
    }

}

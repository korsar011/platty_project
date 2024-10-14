package cards.platty.userservice.repository;

import cards.platty.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findById(Long id);

    @Query("SELECT u FROM UserEntity u WHERE u.oAuthSub = :oAuthSub")
    Optional<UserEntity> findByOAuthSub(String oAuthSub);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.oAuthSub = :oAuthSub")
    Boolean existsByOAuthSub(String oAuthSub);

}

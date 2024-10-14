package cards.platty.flashcardsservice.repository;

import cards.platty.flashcardsservice.entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AudioRepository extends JpaRepository<Audio, Long> {
    Optional<Audio> findByUrl(String url);
}

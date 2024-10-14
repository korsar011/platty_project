package cards.platty.flashcardsservice.repository;

import cards.platty.flashcardsservice.entity.Deck;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    @EntityGraph(attributePaths = {"cards"})
    List<Deck> findAllByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"cards"})
    Optional<Deck> findById(long id);
}

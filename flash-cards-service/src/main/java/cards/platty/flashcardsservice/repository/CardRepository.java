package cards.platty.flashcardsservice.repository;

import cards.platty.flashcardsservice.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findFirstByDeck_IdAndMemoryRatingLessThanEqualAndCountdownDelayAndDateOfReturnBeforeOrderByMemoryRatingAsc(
            long deckId,
            double memoryRating,
            int countdownDelay,
            LocalDateTime currentDateTime
    );

    Optional<Card> findFirstByMemoryRatingLessThanEqualAndCountdownDelayAndOwnerIdAndDateOfReturnBefore(
            double memoryRating,
            int countdownDelay,
            long ownerId,
            LocalDateTime currentDateTime
    );

    @Modifying
    @Query("UPDATE Card c SET c.countdownDelay = c.countdownDelay - 1 WHERE c.countdownDelay > 0 AND c.deck.id = :deckId")
    void decrementAllCountdownDelayByDeckId(long deckId);

    @Modifying
    @Query("UPDATE Card c SET c.countdownDelay = c.countdownDelay - 1 WHERE c.ownerId = :ownerId AND c.countdownDelay > 0")
    void decrementAllCountdownDelayFromAllUserDecks(long ownerId);

    @Modifying
    @Query("UPDATE Card c SET c.countdownDelay = :value WHERE c.countdownDelay > 0 AND c.deck.id = :deckId")
    void setAllCountdownDelayByDeckId(long deckId, int value);

    List<Card> findAllByDeck_IdAndOwnerId(long deckId, long ownerId);

    boolean existsByAudioId(long audioId);

    boolean existsByImageId(long imageId);
}

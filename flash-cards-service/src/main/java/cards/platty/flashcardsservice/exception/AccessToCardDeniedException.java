package cards.platty.flashcardsservice.exception;

public class AccessToCardDeniedException extends RuntimeException {
    public AccessToCardDeniedException(Long cardId, Long userId) {
        super("Unauthorized access to card with id %d from user with id %d".formatted(cardId, userId));
    }
}
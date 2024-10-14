package cards.platty.flashcardsservice.exception;

public class AccessToDeckDeniedException extends RuntimeException {
    public AccessToDeckDeniedException(Long deckId, Long userId) {
        super("Unauthorized access to deck with id %d from user with id %d".formatted(deckId, userId));
    }
}

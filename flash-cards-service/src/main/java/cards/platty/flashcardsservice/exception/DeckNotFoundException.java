package cards.platty.flashcardsservice.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(long deckId) {
        super("Deck with id %d not found".formatted(deckId));
    }
}

package cards.platty.flashcardsservice.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }

    public CardNotFoundException(String message) {
        super(message);
    }
}
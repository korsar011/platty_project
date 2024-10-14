package cards.platty.flashcardsservice.exception;

public class NoCardsLeftException extends RuntimeException {

    public NoCardsLeftException(String message) {
        super(message);
    }
}
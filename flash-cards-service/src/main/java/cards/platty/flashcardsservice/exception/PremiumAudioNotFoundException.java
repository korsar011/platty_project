package cards.platty.flashcardsservice.exception;

public class PremiumAudioNotFoundException extends RuntimeException {
    public PremiumAudioNotFoundException(String message) {
        super(message);
    }

    public PremiumAudioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
package cards.platty.flashcardsservice.exception;

public class AudioNotFoundException extends RuntimeException {
    public AudioNotFoundException(Long audioId) {
        super("Audio with id " + audioId + " not found");
    }

    public AudioNotFoundException(String message) {
        super(message);
    }
}

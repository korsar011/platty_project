package cards.platty.flashcardsservice.exception;

public class MinioDeleteFailedException extends RuntimeException {

    public MinioDeleteFailedException(String message) {
        super(message);
    }

    public MinioDeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

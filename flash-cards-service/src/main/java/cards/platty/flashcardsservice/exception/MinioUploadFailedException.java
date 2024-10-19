package cards.platty.flashcardsservice.exception;

public class MinioUploadFailedException extends RuntimeException {

    public MinioUploadFailedException(String message) {
        super(message);
    }

    public MinioUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

package cards.platty.flashcardsservice.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(Long imageId) {
        super("Image with id %d not found".formatted(imageId));
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}

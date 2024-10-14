package cards.platty.flashcardsservice.handler;

import cards.platty.flashcardsservice.dto.ErrorDto;
import cards.platty.flashcardsservice.exception.AccessToCardDeniedException;
import cards.platty.flashcardsservice.exception.CardNotFoundException;
import cards.platty.flashcardsservice.exception.NoCardsLeftException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessToCardDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessToCardDeniedException(AccessToCardDeniedException ex) {
        log.warn("Access to card exception: {}", ex.getMessage());
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), "ACCESS_DENIED");
        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCardNotFoundException(CardNotFoundException ex) {
        log.warn("Card not found exception: {}", ex.getMessage());
        ErrorDto errorResponse = new ErrorDto(ex.getMessage(), "NOT_FOUND");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoCardsLeftException.class)
    public ResponseEntity<Void> handleNoCardsLeftException(NoCardsLeftException ex) {
        log.warn("No cards left exception: {}", ex.getMessage());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

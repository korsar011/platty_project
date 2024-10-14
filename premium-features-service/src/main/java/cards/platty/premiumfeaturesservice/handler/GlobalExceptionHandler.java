package cards.platty.premiumfeaturesservice.handler;

import cards.platty.premiumfeaturesservice.dto.ErrorDto;
import cards.platty.premiumfeaturesservice.exception.GoogleCredentialsFileReadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GoogleCredentialsFileReadException.class)
    public ResponseEntity<ErrorDto> handleGoogleCredentialsFileReadException(GoogleCredentialsFileReadException ex) {
        log.warn("Google credentials file read exception: {}", ex.getMessage());
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), "READ_ERROR");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

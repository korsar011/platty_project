package cards.platty.authservice.handler;

import cards.platty.authservice.dto.ErrorDto;
import cards.platty.authservice.exception.InvalidCredentialsException;
import cards.platty.authservice.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), "INVALID_CREDENTIALS");
        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
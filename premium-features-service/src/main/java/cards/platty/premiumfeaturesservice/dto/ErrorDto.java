package cards.platty.premiumfeaturesservice.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorDto {

    private String message;

    private String errorCode;

    private LocalDateTime timestamp;

    public ErrorDto(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}

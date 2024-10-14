package cards.platty.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;

    private LocalDateTime expiryDate;
}



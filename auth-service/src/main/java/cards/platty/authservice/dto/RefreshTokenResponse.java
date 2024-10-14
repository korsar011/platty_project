package cards.platty.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
    private String refreshToken;

    private LocalDateTime expiryDate;
}
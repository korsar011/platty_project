package cards.platty.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    private LocalDateTime accessTokenExpiry;

    private LocalDateTime refreshTokenExpiry;

}

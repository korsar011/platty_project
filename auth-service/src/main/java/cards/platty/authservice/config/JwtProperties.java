package cards.platty.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Setter
@Getter
public class JwtProperties implements JwtSecretProvider, JwtTokenValidityProvider {
    private String secret;

    private long accessTokenValidityInMinutes;

    private long refreshTokenValidityInDays;

    @Override
    public long getAccessTokenValidityInMillis() {
        return (long) 60 * 1000 * accessTokenValidityInMinutes;
    }

    @Override
    public long getRefreshTokenValidityInMillis() {
        return (long) 24 * 60 * 60 * 1000 * refreshTokenValidityInDays;
    }

    @Override
    public long getRefreshTokenValidityInDays() {
        return refreshTokenValidityInDays;
    }

    @Override
    public long getAccessTokenValidityInMinutes() {
        return accessTokenValidityInMinutes;
    }
}
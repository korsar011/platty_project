package cards.platty.authservice.config;

public interface JwtTokenValidityProvider {

    long getAccessTokenValidityInMillis();

    long getRefreshTokenValidityInMillis();

    long getAccessTokenValidityInMinutes();

    long getRefreshTokenValidityInDays();
}

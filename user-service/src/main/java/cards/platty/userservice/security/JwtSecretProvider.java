package cards.platty.userservice.security;

public interface JwtSecretProvider {

    String getSecret();
}
package cards.platty.authservice.service;

import cards.platty.authservice.config.JwtSecretProvider;
import cards.platty.authservice.config.JwtTokenValidityProvider;
import cards.platty.authservice.dto.AccessTokenResponse;
import cards.platty.authservice.dto.RefreshTokenResponse;
import cards.platty.authservice.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
public class JwtService {

    private final JwtSecretProvider jwtSecretProvider;

    private final JwtTokenValidityProvider jwtTokenValidityProvider;

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public AccessTokenResponse generateAccessToken(User userCredential) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userCredential.getUsername());
        long tokenValidity = jwtTokenValidityProvider.getAccessTokenValidityInMillis();
        String accessToken = createToken(claims, String.valueOf(userCredential.getId()), tokenValidity);
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(tokenValidity));
        return new AccessTokenResponse(accessToken, expiryDate);
    }

    public RefreshTokenResponse generateRefreshToken(User userCredential) {
        Map<String, Object> claims = new HashMap<>();
        long tokenValidity = jwtTokenValidityProvider.getRefreshTokenValidityInMillis();
        String refreshToken = createToken(claims, String.valueOf(userCredential.getId()), tokenValidity);
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(tokenValidity));
        return new RefreshTokenResponse(refreshToken, expiryDate);
    }

    private String createToken(Map<String, Object> claims, String userId, long validityPeriod) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityPeriod))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretProvider.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

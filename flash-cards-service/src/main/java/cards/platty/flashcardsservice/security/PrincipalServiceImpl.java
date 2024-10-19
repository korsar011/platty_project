package cards.platty.flashcardsservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalServiceImpl implements PrincipalService, JwtTokenProvider {

    @Override
    public Long getPrincipalId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object details = authentication.getDetails();
        if (details instanceof Map) {
            Map<String, Object> authDetails = (Map<String, Object>) details;
            String userId = (String) authDetails.get("userId");
            return Long.valueOf(userId);
        }
        throw new IllegalStateException("User ID not found in authentication details");
    }

    @Override
    public String getPrincipalUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public String getJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object details = authentication.getDetails();
        if (details instanceof Map) {
            Map<String, Object> authDetails = (Map<String, Object>) details;
            return (String) authDetails.get("jwtToken");
        }
        throw new IllegalStateException("JWT token not found in authentication details");
    }
}

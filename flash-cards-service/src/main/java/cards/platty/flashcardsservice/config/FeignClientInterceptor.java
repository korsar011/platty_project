package cards.platty.flashcardsservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> authDetails = (Map<String, Object>) authentication.getDetails();
            String jwtToken = (String) authDetails.get("jwtToken");

            if (jwtToken != null) {
                requestTemplate.header("Authorization", "Bearer " + jwtToken);
            }
        }
    }
}

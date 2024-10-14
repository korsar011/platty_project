package cards.platty.userservice.filter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import cards.platty.userservice.utils.JwtUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            try {
                Claims claims = jwtUtils.getClaimsFromToken(jwtToken);

                String userId = claims.getSubject();
                String username = (String)claims.get("username");

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Map<String, Object> authenticationDetails = new HashMap<>();
                    authenticationDetails.put("userId", userId);
                    authenticationDetails.put("jwtToken", jwtToken);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList());

                    authenticationToken.setDetails(authenticationDetails);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                throw new ServletException("Failed to parse JWT token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}

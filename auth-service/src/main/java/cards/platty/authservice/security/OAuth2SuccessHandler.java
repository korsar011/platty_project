//TODO
//package cards.platty.authservice.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import java.io.IOException;
//import java.util.Collections;
//
//
//@Component
//public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    private final RoleRepository roleRepository;
//    private final UserCredentialRepository userCredentialRepository;
//
//    public OAuth2SuccessHandler(RoleRepository roleRepository, UserCredentialRepository userCredentialRepository) {
//        this.roleRepository = roleRepository;
//        this.userCredentialRepository = userCredentialRepository;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//    Authentication authentication) throws IOException, ServletException {
//        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
//            OAuth2User oauth2User = oauthToken.getPrincipal();
//
//            String email = oauth2User.getAttribute("email");
//            String sub = oauth2User.getAttribute("sub");
//            if (!userCredentialRepository.existsByUsername(sub)) {
//                UserCredential user = new UserCredential();
//                user.setUsername(sub);
//                user.setEmail(email);
//                user.setPassword("");
//                RoleEntity role = roleRepository.findByName("ROLE_USER")
//                        .orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' not found"));
//                user.setRoles(Collections.singletonList(role));
//                userCredentialRepository.save(user);
//            }
//
//            super.onAuthenticationSuccess(request, response, authentication);
//        }
//    }
//}

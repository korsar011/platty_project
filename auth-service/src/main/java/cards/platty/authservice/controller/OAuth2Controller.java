package cards.platty.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/success")
    public String success(Authentication authentication) {
        return "Успешный вход через OAuth2: " + authentication.getName();
    }

    @GetMapping("/failure")
    public String failure() {
        return "Ошибка входа через OAuth2";
    }
}

package cards.platty.premiumfeaturesservice.config;

import cards.platty.premiumfeaturesservice.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({GoogleTranslatorProps.class, JwtProperties.class})
@Configuration
public class AppConfig {
}

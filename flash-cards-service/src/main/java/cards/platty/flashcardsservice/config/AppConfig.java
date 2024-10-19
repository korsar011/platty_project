package cards.platty.flashcardsservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(AppSettingsProviderImpl.class)
@Configuration
public class AppConfig {
}

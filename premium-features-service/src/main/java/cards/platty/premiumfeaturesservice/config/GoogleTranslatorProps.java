package cards.platty.premiumfeaturesservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.translator")
@Getter
@Setter
public class GoogleTranslatorProps implements GoogleCredentialsProvider {

    private String credentialsFilename;
}

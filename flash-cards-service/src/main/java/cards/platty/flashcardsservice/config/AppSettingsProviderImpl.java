package cards.platty.flashcardsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.settings")
@Getter
@Setter
public class AppSettingsProviderImpl implements AppSettingsProvider {

    private double maxMemoryRating;

    private int rewardMark;

    private int penaltyMark;

    private int maxDaysForLearntForever;

    private String defaultFrontText;

    private String defaultBackText;

    private String defaultCardImageUrl;
}

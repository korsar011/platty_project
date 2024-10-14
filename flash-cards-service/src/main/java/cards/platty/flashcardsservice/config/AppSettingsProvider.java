package cards.platty.flashcardsservice.config;

public interface AppSettingsProvider {

    double getMaxMemoryRating();

    int getRewardMark();

    int getPenaltyMark();

    String getDefaultFrontText();

    String getDefaultBackText();

    int getMaxDaysForLearntForever();

    String getDefaultCardImageUrl();
}

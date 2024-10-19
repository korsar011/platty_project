package cards.platty.flashcardsservice.config;

public interface MinioPropertiesProvider {

    String getUrl();

    String getAccessKey();

    String getSecretKey();

    String getImagesBucket();

    String getAudioBucket();

}

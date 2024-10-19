package cards.platty.flashcardsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties implements MinioPropertiesProvider {

    private String url;

    private String accessKey;

    private String secretKey;

    private String imagesBucket;

    private String audioBucket;

}
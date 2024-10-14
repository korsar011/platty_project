package cards.platty.flashcardsservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioPropertiesProvider minioPropertiesProvider;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioPropertiesProvider.getUrl())
                .credentials(minioPropertiesProvider.getAccessKey(), minioPropertiesProvider.getSecretKey())
                .build();
    }
}

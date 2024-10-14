package cards.platty.flashcardsservice.client;

import cards.platty.flashcardsservice.dto.integration.DownloadPremiumImageDtoRequest;
import cards.platty.flashcardsservice.dto.integration.TextToSpeechRequest;
import cards.platty.flashcardsservice.dto.integration.TranslateDtoRequest;
import cards.platty.flashcardsservice.dto.integration.TranslateDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "premium-service")
public interface PremiumFeaturesClient {

    @PutMapping("/api/premium/image")
    ResponseEntity<byte[]> getImage(@RequestBody DownloadPremiumImageDtoRequest downloadImageDtoRequest);

    @PutMapping("/api/premium/translate")
    ResponseEntity<TranslateDtoResponse> translate(@RequestBody TranslateDtoRequest translateDtoRequest);

    @PutMapping("/api/premium/tts")
    ResponseEntity<byte[]> convert(@RequestBody TextToSpeechRequest textToSpeechRequest);
}

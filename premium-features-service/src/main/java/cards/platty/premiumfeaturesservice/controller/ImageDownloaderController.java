package cards.platty.premiumfeaturesservice.controller;

import cards.platty.premiumfeaturesservice.dto.DownloadPremiumImageDtoRequest;
import cards.platty.premiumfeaturesservice.service.ImageDownloaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageDownloaderController {

    private final ImageDownloaderService imageDownloaderService;

    @PutMapping("/api/premium/image")
    public ResponseEntity<byte[]> getImage(@RequestBody DownloadPremiumImageDtoRequest downloadImageDtoRequest) {
        byte[] imageBytes = imageDownloaderService.getImage(downloadImageDtoRequest);

        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(imageBytes);
    }
}
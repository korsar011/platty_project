package cards.platty.flashcardsservice.controller;

import cards.platty.flashcardsservice.dto.ImageDto;
import cards.platty.flashcardsservice.dto.UpdateImageRequestDto;
import cards.platty.flashcardsservice.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/flash/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/for-card")
    public ResponseEntity<ImageDto> uploadCardImage(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("cardId") Long cardId) {
        ImageDto image = imageService.uploadCardImage(file, cardId);
        return ResponseEntity.ok(image);
    }

    @PostMapping("/for-deck")
    public ResponseEntity<ImageDto> uploadDeckImage(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("deckId") Long deckId) {
        ImageDto image = imageService.uploadDeckImage(file, deckId);
        return ResponseEntity.ok(image);
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<ImageDto> updateProfileImage(@RequestParam("file") MultipartFile file,
                                                       @PathVariable Long userId) {
        ImageDto updatedImage = imageService.updateProfileImage(file, userId);
        return ResponseEntity.ok(updatedImage);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDto> getImage(@PathVariable Long imageId) {
        ImageDto image = imageService.getImageById(imageId);
        return ResponseEntity.ok(image);
    }

    @GetMapping
    public ResponseEntity<ImageDto> getImageByUrl(@RequestParam String url) {
        ImageDto image = imageService.getImageByUrl(url);
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/premium/card")
    public ResponseEntity<ImageDto> findAndUploadCardPremiumImageFromText(@Valid @RequestBody UpdateImageRequestDto updateImageDto) {
        ImageDto image = imageService.findAndUploadCardPremiumImageFromText(updateImageDto);
        return ResponseEntity.ok(image);
    }

//    @PostMapping("/premium/deck")
//    public ResponseEntity<ImageDto> findAndUploadDeckPremiumImageFromText(@Valid @RequestBody UpdateImageRequestDto updateImageDto) {
//        ImageDto image = imageService.findAndUploadDeckPremiumImageFromText(updateImageDto);
//        return ResponseEntity.ok(image);
//    }
}
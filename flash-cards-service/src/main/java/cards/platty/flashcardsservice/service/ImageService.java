package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.dto.ImageDto;
import cards.platty.flashcardsservice.dto.UpdateImageRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageDto uploadCardImage(MultipartFile file, Long cardId);

    ImageDto uploadDeckImage(MultipartFile file, Long deckId);

    ImageDto getImageById(Long imageId);

    void deleteImage(Long imageId);

    ImageDto findAndUploadCardPremiumImageFromText(UpdateImageRequestDto updateImageDto);

    ImageDto updateProfileImage(MultipartFile file, Long userId);

    ImageDto getImageByUrl(String url);
}

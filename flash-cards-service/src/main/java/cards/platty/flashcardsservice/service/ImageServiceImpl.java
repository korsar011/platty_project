package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.client.PremiumFeaturesClient;
import cards.platty.flashcardsservice.client.UserFeignClient;
import cards.platty.flashcardsservice.config.MinioPropertiesProvider;
import cards.platty.flashcardsservice.dto.ImageDto;
import cards.platty.flashcardsservice.dto.ImageUrlDto;
import cards.platty.flashcardsservice.dto.UpdateImageRequestDto;
import cards.platty.flashcardsservice.dto.integration.DownloadPremiumImageDtoRequest;
import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.entity.Deck;
import cards.platty.flashcardsservice.entity.Image;
import org.springframework.mock.web.MockMultipartFile;
import cards.platty.flashcardsservice.exception.CardNotFoundException;
import cards.platty.flashcardsservice.exception.DeckNotFoundException;
import cards.platty.flashcardsservice.exception.ImageNotFoundException;
import cards.platty.flashcardsservice.exception.MinioDeleteFailedException;
import cards.platty.flashcardsservice.exception.MinioUploadFailedException;
import cards.platty.flashcardsservice.exception.PremiumImageNotFoundException;
import cards.platty.flashcardsservice.mapper.ImageMapper;
import cards.platty.flashcardsservice.repository.CardRepository;
import cards.platty.flashcardsservice.repository.DeckRepository;
import cards.platty.flashcardsservice.repository.ImageRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;

    private final ImageRepository imageRepository;

    private final CardRepository cardRepository;

    private final DeckRepository deckRepository;

    private final MinioPropertiesProvider minioPropertiesProvider;

    private final ImageMapper imageMapper;

    private final UserFeignClient userFeignClient;
  
    private final PremiumFeaturesClient premiumFeaturesClient;

    @Override
    public ImageDto uploadCardImage(MultipartFile file, Long cardId) {
        return handleCardImageUpload(file, cardId, file.getOriginalFilename());
    }

    @Override
    @Transactional
    public ImageDto uploadDeckImage(MultipartFile file, Long deckId) {

        return handleDeckImageUpload(file, deckId);
    }

    @Override
    public ImageDto findAndUploadCardPremiumImageFromText(UpdateImageRequestDto updateImageDto) {
        ResponseEntity<byte[]> response = premiumFeaturesClient.getImage(new DownloadPremiumImageDtoRequest(updateImageDto.getText()));

        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new PremiumImageNotFoundException("Premium image is not found");
        }

        byte[] imageBytes = response.getBody();
        MultipartFile file = new MockMultipartFile("image", "image.png", "image/png", imageBytes);

        return handleCardImageUpload(file, updateImageDto.getCardId(), "image.png");
    }


    @Override
    @Transactional
    public ImageDto updateProfileImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String objectName = generateObjectName(file);
        String imageUrl;

        try {
            imageUrl = uploadToMinio(file, objectName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setUrl(imageUrl);

        Image savedImage = imageRepository.save(image);

        try {
            userFeignClient.updateUserProfileImage(userId, new ImageUrlDto(imageUrl));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile image", e);
        }

        return imageMapper.entityToDto(savedImage);
    }

    @Override
    public ImageDto getImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(imageId));
        return imageMapper.entityToDto(image);
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(imageId));

        removeImageFromEntities(image);

        String objectName = extractObjectNameFromUrl(image.getUrl());
        deleteFromMinio(objectName);

        imageRepository.delete(image);
    }

    @Override
    public ImageDto getImageByUrl(String url) {
        Image image = imageRepository.findByUrl(url).orElseThrow(() ->
                new ImageNotFoundException("Image with url %s not found".formatted(url)));
        return imageMapper.entityToDto(image);
    }

    private ImageDto handleCardImageUpload(MultipartFile file, Long cardId, String originalFilename) {
        String objectName = generateObjectName(file);
        String imageUrl = uploadToMinio(file, objectName);

        Image image = new Image();
        image.setName(originalFilename);
        image.setUrl(imageUrl);

        Image savedImage = imageRepository.save(image);

        if (cardId != null) {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new CardNotFoundException(cardId));
            card.setImage(savedImage);
            cardRepository.save(card);
        }

        return imageMapper.entityToDto(savedImage);
    }

    private ImageDto handleDeckImageUpload(MultipartFile file, Long deckId) {
        String objectName = file.getOriginalFilename();
        String imageUrl = uploadToMinio(file, objectName);

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setUrl(imageUrl);

        Image savedImage = imageRepository.save(image);

        if (deckId != null) {
            Deck deck = deckRepository.findById(deckId)
                    .orElseThrow(() -> new DeckNotFoundException(deckId));
            deck.setImage(savedImage);
            deckRepository.save(deck);
        }

        return imageMapper.entityToDto(savedImage);
    }

    private void removeImageFromEntities(Image image) {
        cardRepository.findAll().stream()
                .filter(card -> card.getImage() != null && card.getImage().equals(image))
                .forEach(card -> {
                    card.setImage(null);
                    cardRepository.save(card);
                });

        deckRepository.findAll().stream()
                .filter(deck -> deck.getImage() != null && deck.getImage().equals(image))
                .forEach(deck -> {
                    deck.setImage(null);
                    deckRepository.save(deck);
                });
    }

    private String generateObjectName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

    private String uploadToMinio(MultipartFile file, String objectName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioPropertiesProvider.getImagesBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioPropertiesProvider.getImagesBucket())
                    .object(objectName)
                    .method(Method.GET)
                    .build());
        } catch (Exception e) {
            throw new MinioUploadFailedException("Failed to upload image to Minio", e);
        }
    }

    private void deleteFromMinio(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioPropertiesProvider.getImagesBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new MinioDeleteFailedException("Failed to delete image from Minio", e);
        }
    }

    private String extractObjectNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}

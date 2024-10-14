package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.client.PremiumFeaturesClient;
import cards.platty.flashcardsservice.config.MinioPropertiesProvider;
import cards.platty.flashcardsservice.dto.AudioDto;
import cards.platty.flashcardsservice.dto.UpdateAudioRequestDto;
import cards.platty.flashcardsservice.dto.integration.TextToSpeechRequest;
import cards.platty.flashcardsservice.entity.Audio;
import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.exception.*;
import cards.platty.flashcardsservice.mapper.AudioMapper;
import cards.platty.flashcardsservice.repository.AudioRepository;
import cards.platty.flashcardsservice.repository.CardRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {

    private final Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);

    private final MinioClient minioClient;
    private final AudioRepository audioRepository;
    private final CardRepository cardRepository;
    private final MinioPropertiesProvider minioPropertiesProvider;
    private final AudioMapper audioMapper;
    private final PremiumFeaturesClient premiumFeaturesClient;

    @Override
    public AudioDto findAndUploadCardPremiumAudioFromText(UpdateAudioRequestDto updateAudioDto) {
        ResponseEntity<byte[]> response = premiumFeaturesClient.convert(new TextToSpeechRequest(updateAudioDto.getText()));

        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new PremiumAudioNotFoundException("Premium audio is not found");
        }

        byte[] audioBytes = response.getBody();
        MultipartFile file = new MockMultipartFile("audio", "audio.ogg", "audio/ogg", audioBytes);

        logger.info("Uploading audio for cardId: {}", updateAudioDto.getCardId());
        return uploadAudio(updateAudioDto.getCardId(), file);
    }

    @Override
    public AudioDto uploadAudio(Long cardId, MultipartFile file) {
        Card card = getCardById(cardId);

        if (card.getAudio() != null) {
            deleteAudio(card.getAudio().getId());
        }

        String objectName = generateObjectName(file);
        String audioUrl = uploadToMinio(file, objectName);

        return saveAudioToCard(card, file, audioUrl);
    }

    @Override
    public AudioDto getAudioById(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new AudioNotFoundException(audioId));
        return audioMapper.entityToDto(audio);
    }

    @Override
    public void deleteAudio(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new AudioNotFoundException(audioId));

        String objectName = extractObjectNameFromUrl(audio.getUrl());
        deleteFromMinio(objectName);
        audioRepository.delete(audio);
    }

    private Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    private AudioDto saveAudioToCard(Card card, MultipartFile file, String fileUrl) {
        Audio audio = new Audio();
        audio.setName(file.getOriginalFilename());
        audio.setUrl(fileUrl);
        audio.setContentType(file.getContentType());
        audio.setSize(file.getSize());

        Audio savedAudio = audioRepository.save(audio);

        card.setAudio(savedAudio);
        cardRepository.save(card);

        return audioMapper.entityToDto(savedAudio);
    }

    private String generateObjectName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        return UUID.randomUUID().toString() + "_" + sanitizedFilename;
    }

    private String uploadToMinio(MultipartFile file, String objectName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioPropertiesProvider.getAudioBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioPropertiesProvider.getAudioBucket())
                    .object(objectName)
                    .method(Method.GET)
                    .build());
        } catch (Exception e) {
            throw new MinioUploadFailedException("Failed to upload audio to Minio", e);
        }
    }

    private void deleteFromMinio(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioPropertiesProvider.getAudioBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new MinioDeleteFailedException("Failed to delete audio from Minio", e);
        }
    }

    private String extractObjectNameFromUrl(String audioUrl) {
        return audioUrl.substring(audioUrl.lastIndexOf("/") + 1);
    }
}
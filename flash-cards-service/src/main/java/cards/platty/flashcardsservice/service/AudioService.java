package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.dto.AudioDto;
import cards.platty.flashcardsservice.dto.UpdateAudioRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface AudioService {
    AudioDto findAndUploadCardPremiumAudioFromText(UpdateAudioRequestDto updateAudioDto);

    AudioDto uploadAudio(Long cardId, MultipartFile file);

    AudioDto getAudioById(Long audioId);

    void deleteAudio(Long audioId);
}
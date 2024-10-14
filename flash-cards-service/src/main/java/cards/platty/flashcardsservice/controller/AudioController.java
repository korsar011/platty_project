package cards.platty.flashcardsservice.controller;

import cards.platty.flashcardsservice.dto.AudioDto;
import cards.platty.flashcardsservice.dto.UpdateAudioRequestDto;
import cards.platty.flashcardsservice.dto.UpdateImageRequestDto;
import cards.platty.flashcardsservice.service.AudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flash/audio")
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/generate")
    public ResponseEntity<AudioDto> generateVoiceForCard(@Valid @RequestBody UpdateAudioRequestDto updateAudioRequestDto) {
        AudioDto audio = audioService.findAndUploadCardPremiumAudioFromText(updateAudioRequestDto);
        return ResponseEntity.ok(audio);
    }

    @PostMapping("/upload")
    public ResponseEntity<AudioDto> uploadVoiceForCard(@RequestParam("cardId") Long cardId,
                                                       @RequestParam("file") MultipartFile audioFile) {
        AudioDto audio = audioService.uploadAudio(cardId, audioFile);
        return ResponseEntity.ok(audio);
    }

}


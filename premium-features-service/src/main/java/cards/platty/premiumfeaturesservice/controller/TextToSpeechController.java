package cards.platty.premiumfeaturesservice.controller;

import cards.platty.premiumfeaturesservice.dto.TextToSpeechRequest;
import cards.platty.premiumfeaturesservice.service.TextToSpeechConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TextToSpeechController {

    private final TextToSpeechConverterService textToSpeechConverterService;

    @PutMapping("/api/premium/tts")
    public ResponseEntity<byte[]> convert(@RequestBody TextToSpeechRequest textToSpeechRequest) {
        if (textToSpeechRequest == null || textToSpeechRequest.getText() == null || textToSpeechRequest.getText().isEmpty()) {
            return ResponseEntity.badRequest().body("Text cannot be null or empty".getBytes());
        }

        byte[] audioBytes = textToSpeechConverterService.convertTextToSpeech(textToSpeechRequest);
        if (audioBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "audio/ogg")
                .body(audioBytes);
    }
}
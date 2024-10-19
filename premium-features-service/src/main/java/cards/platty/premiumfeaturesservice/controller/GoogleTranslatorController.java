package cards.platty.premiumfeaturesservice.controller;

import cards.platty.premiumfeaturesservice.dto.TranslateDtoRequest;
import cards.platty.premiumfeaturesservice.dto.TranslateDtoResponse;
import cards.platty.premiumfeaturesservice.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleTranslatorController {

    private final TranslatorService translatorService;

    @PutMapping("/api/premium/translate")
    public ResponseEntity<TranslateDtoResponse> translate(@RequestBody TranslateDtoRequest translateDtoRequest) {
        return ResponseEntity.ok(translatorService.translate(translateDtoRequest));
    }
}

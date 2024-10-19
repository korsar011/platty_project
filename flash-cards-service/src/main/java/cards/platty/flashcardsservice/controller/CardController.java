package cards.platty.flashcardsservice.controller;

import cards.platty.flashcardsservice.dto.CardDto;
import cards.platty.flashcardsservice.dto.CreateCardRequest;
import cards.platty.flashcardsservice.dto.TranslateCardFrontTextRequest;
import cards.platty.flashcardsservice.dto.UpdateCardRatingRequest;
import cards.platty.flashcardsservice.dto.UpdateCardRequest;
import cards.platty.flashcardsservice.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/flash/cards")
public class CardController {

    private final CardService cardService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable @Positive Long id) {
        CardDto cardDto = cardService.getCardById(id);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping(value = "/next")
    public ResponseEntity<CardDto> getNextCardFromDeck(@RequestParam @Positive Long deckId) {
        CardDto cardDto = cardService.getNextCardFromDeck(deckId);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping(value = "/master-next")
    public ResponseEntity<CardDto> getNextCardFromAllUserDecks() {
        CardDto cardDto = cardService.getNextCardFromAllUserDecks();
        return ResponseEntity.ok(cardDto);
    }

    @PutMapping(value = "/{cardId}/rating")
    public ResponseEntity<Void> updateCardRating(
            @PathVariable @Positive Long cardId,
            @Valid @RequestBody UpdateCardRatingRequest updateCardRatingRequest) {
        cardService.updateCardRating(cardId, updateCardRatingRequest.getUserChoice());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CardDto>> getAllCardsByDeckId(@RequestParam @Positive Long deckId) {
        List<CardDto> cards = cardService.getAllCardsByDeckId(deckId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CreateCardRequest createCardRequest) {
        CardDto cardDto = cardService.createCard(createCardRequest);
        return ResponseEntity.status(201).body(cardDto);
    }

    @PutMapping(value = "/{cardId}")
    public ResponseEntity<CardDto> updateCard(
            @PathVariable @Positive Long cardId,
            @Valid @RequestBody UpdateCardRequest updateCardRequest) {
        CardDto cardDto = cardService.updateCard(cardId, updateCardRequest);
        return ResponseEntity.ok(cardDto);
    }

    @DeleteMapping(value = "/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable @Positive Long cardId) {
        cardService.deleteCardById(cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/default")
    public ResponseEntity<CardDto> createDefaultCard(@RequestParam @Positive Long deckId) {
        CardDto cardDto = cardService.createDefaultCard(deckId);
        return ResponseEntity.status(201).body(cardDto);
    }

    @PutMapping(value = "/translate")
    public ResponseEntity<CardDto> updateCardFrontTextFromBackTextTranslation(
            @Valid @RequestBody TranslateCardFrontTextRequest translateCardFrontTextRequest) {
        CardDto updatedCard = cardService.updateCardBackTextTranslationFromFrontText(translateCardFrontTextRequest);
        return ResponseEntity.ok(updatedCard);
    }
}
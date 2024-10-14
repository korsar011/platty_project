package cards.platty.flashcardsservice.controller;

import cards.platty.flashcardsservice.dto.CreateDeckRequest;
import cards.platty.flashcardsservice.dto.DeckDto;
import cards.platty.flashcardsservice.dto.DeckInfoDto;
import cards.platty.flashcardsservice.dto.UpdateDeckRequest;
import cards.platty.flashcardsservice.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/flash/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping
    public ResponseEntity<List<DeckDto>> getAllUserDecks() {
        List<DeckDto> decks = deckService.getAllUserDecks();
        return ResponseEntity.ok(decks);
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<DeckDto> getDeckById(@PathVariable Long deckId) {
        DeckDto deckDto = deckService.getDeckById(deckId);
        return ResponseEntity.ok(deckDto);
    }

    @PostMapping
    public ResponseEntity<DeckDto> createDeck(@RequestBody CreateDeckRequest createDeckRequest) {
        DeckDto createdDeck = deckService.createDeck(createDeckRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeck);
    }

    @GetMapping("/info")
    public ResponseEntity<DeckInfoDto> getDeckInfo(@RequestParam Long deckId) {
        return ResponseEntity.ok(deckService.getDeckInfo(deckId));
    }

    @PutMapping("/{deckId}")
    public ResponseEntity<DeckDto> updateDeck(
            @PathVariable Long deckId,
            @RequestBody UpdateDeckRequest updateDeckRequest) {
        DeckDto updatedDeck = deckService.updateDeck(deckId, updateDeckRequest);
        return ResponseEntity.ok(updatedDeck);
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long deckId) {
        deckService.deleteDeckById(deckId);
        return ResponseEntity.noContent().build();
    }
}
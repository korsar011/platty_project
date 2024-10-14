package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.dto.CreateDeckRequest;
import cards.platty.flashcardsservice.dto.DeckDto;
import cards.platty.flashcardsservice.dto.DeckInfoDto;
import cards.platty.flashcardsservice.dto.UpdateDeckRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DeckService {
    DeckDto getDeckById(long deckId);

    @Transactional(readOnly = true)
    List<DeckDto> getAllUserDecks();

    @Transactional
    DeckDto createDeck(CreateDeckRequest createDeckRequest);

    @Transactional
    DeckDto updateDeck(Long deckId, UpdateDeckRequest updateDeckRequest);

    @Transactional
    void deleteDeckById(Long deckId);

    DeckInfoDto getDeckInfo(Long deckId);
}

package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.dto.CardDto;
import cards.platty.flashcardsservice.dto.CreateCardRequest;
import cards.platty.flashcardsservice.dto.TranslateCardFrontTextRequest;
import cards.platty.flashcardsservice.dto.UpdateCardRequest;
import cards.platty.flashcardsservice.entity.UserChoice;

import java.util.List;

public interface CardService {

    CardDto getCardById(long id);

    CardDto getNextCardFromDeck(Long deckId);

    CardDto getNextCardFromAllUserDecks();

    void updateCardRating(Long cardId, UserChoice userChoice);

    CardDto createCard(CreateCardRequest createCardRequest);

    CardDto updateCard(Long cardId, UpdateCardRequest updateCardRequest);

    void deleteCardById(Long id);

    CardDto createDefaultCard(Long deckId);

    List<CardDto> getAllCardsByDeckId(long deckId);

    CardDto updateCardBackTextTranslationFromFrontText(TranslateCardFrontTextRequest translateCardBackTextRequest);
}

package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.component.DeckAnalyzer;
import cards.platty.flashcardsservice.dto.CreateDeckRequest;
import cards.platty.flashcardsservice.dto.DeckDto;
import cards.platty.flashcardsservice.dto.DeckInfoDto;
import cards.platty.flashcardsservice.dto.UpdateDeckRequest;
import cards.platty.flashcardsservice.entity.Deck;
import cards.platty.flashcardsservice.exception.DeckNotFoundException;
import cards.platty.flashcardsservice.mapper.DeckMapper;
import cards.platty.flashcardsservice.repository.DeckRepository;
import cards.platty.flashcardsservice.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;

    private final DeckMapper deckMapper;

    private final AuthorizationService authorizationService;

    private final DeckAnalyzer deckAnalyzer;

    @Override
    public DeckDto getDeckById(long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));
        authorizationService.authorizeDeckAccess(deck);
        updateDeckInfo(deck);
        return deckMapper.entityToDto(deck);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckDto> getAllUserDecks() {
        Long userId = authorizationService.getCurrentUserId();
        return deckRepository.findAllByOwnerId(userId)
                .stream()
                .peek(this::updateDeckInfo)
                .map(deckMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional
    public DeckDto createDeck(CreateDeckRequest createDeckRequest) {
        Long userId = authorizationService.getCurrentUserId();

        Deck deck = Deck.builder()
                .name(createDeckRequest.getName())
                .description(createDeckRequest.getDescription())
                .type(createDeckRequest.getType())
                .size(0)
                .cards(new ArrayList<>())
                .cardsLearnt(0)
                .cardsLeft(0)
                .ownerId(userId)
                .build();

        return deckMapper.entityToDto(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public DeckDto updateDeck(Long deckId, UpdateDeckRequest updateDeckRequest) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));

        authorizationService.authorizeDeckAccess(deck);

        if (updateDeckRequest.getName() != null) {
            deck.setName(updateDeckRequest.getName());
        }

        if (updateDeckRequest.getDescription() != null) {
            deck.setDescription(updateDeckRequest.getDescription());
        }

        if (updateDeckRequest.getType() != null) {
            deck.setType(updateDeckRequest.getType());
        }

        updateDeckInfo(deck);
        return deckMapper.entityToDto(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public void deleteDeckById(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));

        authorizationService.authorizeDeckAccess(deck);

        deckRepository.deleteById(deckId);
    }

    @Override
    public DeckInfoDto getDeckInfo(Long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(() ->
                new DeckNotFoundException(deckId));
        authorizationService.authorizeDeckAccess(deck);
        return new DeckInfoDto(deckAnalyzer.calculateSize(deck),
                deckAnalyzer.calculateCardsLearnt(deck),
                deckAnalyzer.calculateCardsLeft(deck));
    }

    private void updateDeckInfo(Deck deck) {
        deck.setSize(deckAnalyzer.calculateSize(deck));
        deck.setCardsLearnt(deckAnalyzer.calculateCardsLearnt(deck));
        deck.setCardsLeft(deckAnalyzer.calculateCardsLeft(deck));
    }

}

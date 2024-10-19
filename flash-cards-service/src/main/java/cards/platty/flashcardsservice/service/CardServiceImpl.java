package cards.platty.flashcardsservice.service;

import cards.platty.flashcardsservice.client.PremiumFeaturesClient;
import cards.platty.flashcardsservice.config.AppSettingsProvider;
import cards.platty.flashcardsservice.dto.CardDto;
import cards.platty.flashcardsservice.dto.CreateCardRequest;
import cards.platty.flashcardsservice.dto.TranslateCardFrontTextRequest;
import cards.platty.flashcardsservice.dto.UpdateCardRequest;
import cards.platty.flashcardsservice.dto.integration.TranslateDtoRequest;
import cards.platty.flashcardsservice.dto.integration.TranslateDtoResponse;
import cards.platty.flashcardsservice.entity.Audio;
import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.entity.Deck;
import cards.platty.flashcardsservice.entity.Image;
import cards.platty.flashcardsservice.entity.UserChoice;
import cards.platty.flashcardsservice.exception.AudioNotFoundException;
import cards.platty.flashcardsservice.exception.BackTextTranslationException;
import cards.platty.flashcardsservice.exception.CardNotFoundException;
import cards.platty.flashcardsservice.exception.DeckNotFoundException;
import cards.platty.flashcardsservice.exception.ImageNotFoundException;
import cards.platty.flashcardsservice.exception.NoCardsLeftException;
import cards.platty.flashcardsservice.mapper.CardMapper;
import cards.platty.flashcardsservice.repository.AudioRepository;
import cards.platty.flashcardsservice.repository.CardRepository;
import cards.platty.flashcardsservice.repository.DeckRepository;
import cards.platty.flashcardsservice.repository.ImageRepository;
import cards.platty.flashcardsservice.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private static final int MINIMUM_CARDS_FOR_NO_DELAY = 2;

    private static final int NUMBER_OF_TURNS_TO_DELAY_CARD = 10;

    private final AppSettingsProvider appSettingsProvider;

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final DeckRepository deckRepository;

    private final ImageRepository imageRepository;

    private final AudioRepository audioRepository;

    private final AuthorizationService authorizationService;
  
    private final PremiumFeaturesClient premiumFeaturesClient;


    @Override
    public CardDto getCardById(long id) {
        Card card = findCardById(id);
        authorizationService.authorizeCardAccess(card);
        return cardMapper.entityToDto(card);
    }

    @Override
    @Transactional
    public CardDto getNextCardFromDeck(Long deckId) {
        cardRepository.decrementAllCountdownDelayByDeckId(deckId);
        Card card = findNextCard(deckId);
        authorizationService.authorizeCardAccess(card);
        return cardMapper.entityToDto(card);
    }

    @Override
    @Transactional
    public CardDto getNextCardFromAllUserDecks() {
        Long userId = authorizationService.getCurrentUserId();
        cardRepository.decrementAllCountdownDelayFromAllUserDecks(userId);
        Card card = findNextCardFromAllUserDecks(userId);
        return cardMapper.entityToDto(card);
    }

    @Override
    @Transactional
    public void updateCardRating(Long cardId, UserChoice userChoice) {
        int mark = calculateMark(userChoice);
        Card card = findCardById(cardId);
        updateCardMemoryRating(card, mark);
        updateCountdownDelay(card);
        if (card.getMemoryRating() >= appSettingsProvider.getMaxMemoryRating()) {
            updateDateOfReturn(card);
        }
        cardRepository.save(card);
    }

    @Override
    public List<CardDto> getAllCardsByDeckId (long deckId) {
        Long userId = authorizationService.getCurrentUserId();
        return cardRepository.findAllByDeck_IdAndOwnerId(deckId, userId)
                .stream()
                .map(cardMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional
    public CardDto createCard(CreateCardRequest createCardRequest) {
        Long deckId = createCardRequest.getDeckId();
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));
        authorizationService.authorizeDeckAccess(deck);

        Card card = buildCard(createCardRequest, deck);
        deck.addCard(card);
        return cardMapper.entityToDto(cardRepository.save(card));
    }

    @Override
    @Transactional
    public CardDto updateCard(Long cardId, UpdateCardRequest updateCardRequest) {
        Card card = findCardById(cardId);
        authorizationService.authorizeCardAccess(card);
        updateCardDetails(card, updateCardRequest);
        return cardMapper.entityToDto(cardRepository.save(card));
    }

    @Override
    public void deleteCardById(Long cardId) {
        Card card = findCardById(cardId);
        authorizationService.authorizeCardAccess(card);

        if (card.getAudio() != null) {
            Long audioId = card.getAudio().getId();
            boolean isAudioReferenced = cardRepository.existsByAudioId(audioId);
            if (!isAudioReferenced) {
                audioRepository.deleteById(audioId);
            }
        }

        if (card.getImage() != null) {
            Long imageId = card.getImage().getId();
            boolean isImageReferenced = cardRepository.existsByImageId(imageId);
            if (!isImageReferenced) {
                imageRepository.deleteById(imageId);
            }
        }

        cardRepository.deleteById(cardId);
    }


    @Override
    @Transactional
    public CardDto createDefaultCard(Long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(() ->
                new DeckNotFoundException(deckId));
        authorizationService.authorizeDeckAccess(deck);

        Card card = Card.builder()
                .deck(deck)
                .frontText(appSettingsProvider.getDefaultFrontText())
                .backText(appSettingsProvider.getDefaultBackText())
                .countdownDelay(0)
                .memoryRating(0.0)
                .dateOfReturn(LocalDateTime.now())
                .image(new Image(appSettingsProvider.getDefaultCardImageUrl()))
                .ownerId(authorizationService.getCurrentUserId())
                .build();

        deck.addCard(card);
        return cardMapper.entityToDto(cardRepository.save(card));
    }

    @Override
    public CardDto updateCardBackTextTranslationFromFrontText(
            TranslateCardFrontTextRequest translateCardBackTextRequest) {
        Long cardId = translateCardBackTextRequest.getCardId();
        Card card = findCardById(cardId);
        authorizationService.authorizeCardAccess(card);

        String translation = translateFrontText(card.getFrontText(), translateCardBackTextRequest.getTargetLanguage());
        card.setBackText(translation);

        return cardMapper.entityToDto(card);
    }

    private String translateFrontText(String frontText, String targetLanguage) {
        Optional<TranslateDtoResponse> translateDtoResponseOptional = Optional.ofNullable(
                premiumFeaturesClient.translate(new TranslateDtoRequest(frontText, targetLanguage)).getBody());
        if (translateDtoResponseOptional.isEmpty()) {
            throw new BackTextTranslationException("Error translating back text");
        }
        return translateDtoResponseOptional.get().getTranslatedText();
    }

    private Card buildCard(CreateCardRequest request, Deck deck) {
        String imageUrl = request.getImageUrl();
        String audioUrl = request.getAudioUrl();
        Image image = imageUrl == null ? null : imageRepository.findByUrl(imageUrl).orElseThrow(() ->
                        new ImageNotFoundException("Image with url %s not found".formatted(imageUrl)));
        Audio audio = audioUrl == null ? null : audioRepository.findByUrl(audioUrl).orElseThrow(() ->
                new AudioNotFoundException("Audio with url %s not found".formatted(audioUrl)));

        Card card = new Card();
        card.setFrontText(request.getFrontText());
        card.setBackText(request.getBackText());
        card.setImage(image);
        card.setAudio(audio);
        card.setDeck(deck);
        card.setOwnerId(authorizationService.getCurrentUserId());
        card.setCountdownDelay(0);
        card.setMemoryRating(0.0);

        return card;
    }

    private void updateCardDetails(Card card, UpdateCardRequest request) {
        if (request.getDeckId() != null) {
            Deck deck = deckRepository.findById(request.getDeckId())
                    .orElseThrow(() -> new DeckNotFoundException(request.getDeckId()));
            card.setDeck(deck);
        }

        if (request.getFrontText() != null) {
            card.setFrontText(request.getFrontText());
        }

        if (request.getBackText() != null) {
            card.setBackText(request.getBackText());
        }

        String imageUrl = request.getImageUrl();
        if (imageUrl != null) {
            Image image = imageRepository.findByUrl(imageUrl).orElseThrow(() ->
                    new ImageNotFoundException("Image with url %s not found".formatted(imageUrl)));
            card.setImage(image);
        }
    }

    private int calculateMark(UserChoice userChoice) {
        return switch (userChoice) {
            case REMEMBER -> appSettingsProvider.getRewardMark();
            case FORGOT -> appSettingsProvider.getPenaltyMark();
        };
    }

    private Card updateCardMemoryRating(Card card, int mark) {
        card.setMemoryRating(calculateAverage(card.getMemoryRating(), mark));
        return card;
    }

    private Card updateDateOfReturn(Card card) {
        int days = card.getDaysToDelay();
        if (days >= appSettingsProvider.getMaxDaysForLearntForever()) {
            card.setLearntForever(true);
        } else {
            days = days == 0 ? 1 : days * 2;
            card.setDaysToDelay(days);
            LocalDateTime dateOfReturn = LocalDateTime.now().plus(Duration.of(days, ChronoUnit.DAYS));
            card.setDateOfReturn(dateOfReturn);
            card.setMemoryRating(0.0);
            card.setCountdownDelay(0);
        }
        return card;
    }

    private void updateCountdownDelay(Card card) {
        int cardsLeft = card.getDeck().getCardsLeft();
        if (cardsLeft <= MINIMUM_CARDS_FOR_NO_DELAY) {
            cardRepository.setAllCountdownDelayByDeckId(card.getDeck().getId(), 0);
        } else {
            card.setCountdownDelay(Math.min(cardsLeft, NUMBER_OF_TURNS_TO_DELAY_CARD));
        }
    }

    private double calculateAverage(double currentRating, double newRating) {
        return (currentRating + newRating) / 2.0;
    }

    private Card findNextCardFromAllUserDecks(Long userId) {
        return cardRepository
                .findFirstByMemoryRatingLessThanEqualAndCountdownDelayAndOwnerIdAndDateOfReturnBefore(
                        appSettingsProvider.getMaxMemoryRating(), 0, userId, LocalDateTime.now())
                .orElseThrow(() -> new CardNotFoundException("No cards found for user decks."));
    }

    private Card findCardById(long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    private Card findNextCard(Long deckId) {
        return cardRepository.findFirstByDeck_IdAndMemoryRatingLessThanEqualAndCountdownDelayAndDateOfReturnBeforeOrderByMemoryRatingAsc(
                        deckId, appSettingsProvider.getMaxMemoryRating(), 0, LocalDateTime.now())
                .orElseThrow(() -> new NoCardsLeftException("Card that satisfies the requirements not found"));
    }
}

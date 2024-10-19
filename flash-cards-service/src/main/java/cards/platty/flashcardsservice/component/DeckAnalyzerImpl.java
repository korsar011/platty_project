package cards.platty.flashcardsservice.component;

import cards.platty.flashcardsservice.config.AppSettingsProvider;
import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.entity.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DeckAnalyzerImpl implements DeckAnalyzer {

    private final AppSettingsProvider appSettingsProvider;

    @Override
    public int calculateCardsLearnt(Deck deck) {
        int learntCount = 0;
        for (Card card : deck.getCards()) {
            if (card.getMemoryRating() >= appSettingsProvider.getMaxMemoryRating() ||
                    card.getDateOfReturn().isAfter(LocalDateTime.now())) {
                learntCount++;
            }
        }
        return learntCount;
    }

    @Override
    public int calculateCardsLeft(Deck deck) {
        return calculateSize(deck) - calculateCardsLearnt(deck);
    }

    @Override
    public int calculateSize(Deck deck) {
        return deck.getCards().size();
    }
}

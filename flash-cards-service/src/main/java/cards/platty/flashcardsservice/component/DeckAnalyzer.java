package cards.platty.flashcardsservice.component;

import cards.platty.flashcardsservice.entity.Deck;

public interface DeckAnalyzer {

    int calculateCardsLearnt(Deck deck);

    int calculateCardsLeft(Deck deck);

    int calculateSize(Deck deck);
}

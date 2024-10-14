package cards.platty.flashcardsservice.security;

import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.entity.Deck;

public interface AuthorizationService {

    void authorizeCardAccess(Card card);

    void authorizeDeckAccess(Deck deck);

    Long getCurrentUserId();
}

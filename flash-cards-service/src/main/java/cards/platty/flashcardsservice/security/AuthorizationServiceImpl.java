package cards.platty.flashcardsservice.security;

import cards.platty.flashcardsservice.entity.Card;
import cards.platty.flashcardsservice.entity.Deck;
import cards.platty.flashcardsservice.exception.AccessToCardDeniedException;
import cards.platty.flashcardsservice.exception.AccessToDeckDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final PrincipalService principalService;

    @Override
    public void authorizeCardAccess(Card card) {
        Long userId = principalService.getPrincipalId();
        if (!card.getOwnerId().equals(userId)) {
            throw new AccessToCardDeniedException(card.getId(), userId);
        }
    }

    @Override
    public void authorizeDeckAccess(Deck deck) {
        Long userId = principalService.getPrincipalId();
        if (!deck.getOwnerId().equals(userId)) {
            throw new AccessToDeckDeniedException(deck.getId(), userId);
        }
    }

    @Override
    public Long getCurrentUserId() {
        return principalService.getPrincipalId();
    }
}

package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeckInfoDto {

    private int deckSize;

    private int cardsLearnt;

    private int cardsLeft;
}

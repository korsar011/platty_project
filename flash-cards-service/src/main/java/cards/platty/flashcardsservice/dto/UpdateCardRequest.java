package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCardRequest {
    private String frontText;

    private String backText;

    private String imageUrl;

    private String oggUrl;

    private Long deckId;
}
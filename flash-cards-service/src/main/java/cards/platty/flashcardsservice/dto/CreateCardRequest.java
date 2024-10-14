package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardRequest {
    private String frontText;

    private String backText;

    private String imageUrl;

    private String audioUrl;

    private Long deckId;
}

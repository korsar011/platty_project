package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeckDto {

    private Long id;

    private String name;

    private int cardsLearnt;

    private int cardsLeft;

    private String type;

    private String description;

    private int size;

    private ImageDto image;

    private String audioUrl;

    private List<CardDto> cards;
}

package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAudioRequestDto {
    private Long cardId;

    private String text;
}

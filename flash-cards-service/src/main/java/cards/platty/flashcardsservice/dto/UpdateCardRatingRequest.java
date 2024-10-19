package cards.platty.flashcardsservice.dto;

import cards.platty.flashcardsservice.entity.UserChoice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCardRatingRequest {

    @NotNull(message = "User choice must not be null")
    private UserChoice userChoice;
}
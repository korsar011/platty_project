package cards.platty.flashcardsservice.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslateDtoRequest {

    private String text;

    private String targetLanguage;
}

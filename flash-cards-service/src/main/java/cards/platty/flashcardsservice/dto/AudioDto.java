package cards.platty.flashcardsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioDto {
    private Long id;

    private String name;

    private String url;

    private String contentType;

    private Long size;
}

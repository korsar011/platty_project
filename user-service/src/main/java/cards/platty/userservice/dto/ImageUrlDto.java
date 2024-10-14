package cards.platty.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ImageUrlDto {
    private String imageUrl;

    public ImageUrlDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
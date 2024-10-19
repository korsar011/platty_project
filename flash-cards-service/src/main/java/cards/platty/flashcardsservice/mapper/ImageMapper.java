package cards.platty.flashcardsservice.mapper;

import cards.platty.flashcardsservice.dto.ImageDto;
import cards.platty.flashcardsservice.entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto entityToDto(Image image);
}

package cards.platty.flashcardsservice.mapper;

import cards.platty.flashcardsservice.dto.AudioDto;
import cards.platty.flashcardsservice.entity.Audio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AudioMapper {

    AudioDto entityToDto(Audio audio);
}

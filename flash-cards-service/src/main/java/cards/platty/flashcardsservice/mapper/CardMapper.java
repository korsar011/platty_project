package cards.platty.flashcardsservice.mapper;

import cards.platty.flashcardsservice.dto.CardDto;
import cards.platty.flashcardsservice.entity.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ImageMapper.class, AudioMapper.class})
public interface CardMapper {

    CardDto entityToDto(Card card);
}

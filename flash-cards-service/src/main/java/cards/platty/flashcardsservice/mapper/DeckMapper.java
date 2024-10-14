package cards.platty.flashcardsservice.mapper;

import cards.platty.flashcardsservice.dto.DeckDto;
import cards.platty.flashcardsservice.entity.Deck;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {CardMapper.class, ImageMapper.class})
public interface DeckMapper {

    DeckDto entityToDto(Deck deck);

}

package cards.platty.premiumfeaturesservice.service;


import cards.platty.premiumfeaturesservice.dto.TranslateDtoRequest;
import cards.platty.premiumfeaturesservice.dto.TranslateDtoResponse;

public interface TranslatorService {

    TranslateDtoResponse translate(TranslateDtoRequest translateDtoRequest);
}


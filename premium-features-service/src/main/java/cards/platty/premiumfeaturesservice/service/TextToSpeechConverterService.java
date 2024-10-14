package cards.platty.premiumfeaturesservice.service;

import cards.platty.premiumfeaturesservice.dto.TextToSpeechRequest;
import org.springframework.web.multipart.MultipartFile;

public interface TextToSpeechConverterService {

    byte[] convertTextToSpeech (TextToSpeechRequest textToSpeechRequest);

}

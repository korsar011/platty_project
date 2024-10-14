package cards.platty.premiumfeaturesservice.service;

import cards.platty.premiumfeaturesservice.config.GoogleCredentialsProvider;
import cards.platty.premiumfeaturesservice.dto.TranslateDtoRequest;
import cards.platty.premiumfeaturesservice.dto.TranslateDtoResponse;
import cards.platty.premiumfeaturesservice.exception.GoogleCredentialsFileReadException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class GoogleTranslatorService implements TranslatorService {

    private final GoogleCredentialsProvider googleCredentialsProvider;

    @Override
    public TranslateDtoResponse translate(TranslateDtoRequest translateDtoRequest) {
        try {
            String credentialsFilename = googleCredentialsProvider.getCredentialsFilename();
            InputStream credentialsStream = getClass().getResourceAsStream("/" + credentialsFilename);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

            Translate translate = TranslateOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

            Translation translation = translate.translate(translateDtoRequest.getText(),
                    Translate.TranslateOption.targetLanguage(translateDtoRequest.getTargetLanguage()));

            return new TranslateDtoResponse(translation.getTranslatedText());
        } catch (IOException e) {
            throw new GoogleCredentialsFileReadException("Error reading credentials file");
        }
    }
}

package cards.platty.premiumfeaturesservice.service;

import cards.platty.premiumfeaturesservice.config.GoogleCredentialsProvider;
import cards.platty.premiumfeaturesservice.dto.TextToSpeechRequest;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class GoogleTextToSpeechConverterService implements TextToSpeechConverterService {

    private final GoogleCredentialsProvider googleCredentialsProvider;

    @Override
    public byte[] convertTextToSpeech(TextToSpeechRequest textToSpeechRequest) {
        GoogleCredentials credentials;
        try {
            credentials = getGoogleCredentials();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try (TextToSpeechClient textToSpeechClient = getTextToSpeechClient(credentials)) {
            return synthesizeSpeech(textToSpeechClient, textToSpeechRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private GoogleCredentials getGoogleCredentials() throws IOException {
        String credentialsFilename = googleCredentialsProvider.getCredentialsFilename();
        InputStream credentialsStream = getClass().getResourceAsStream("/" + credentialsFilename);
        return GoogleCredentials.fromStream(credentialsStream);
    }

    private TextToSpeechClient getTextToSpeechClient(GoogleCredentials credentials) throws IOException {
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        return TextToSpeechClient.create(settings);
    }

    private byte[] synthesizeSpeech(TextToSpeechClient textToSpeechClient, TextToSpeechRequest textToSpeechRequest) throws IOException {
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(textToSpeechRequest.getText())
                .build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.FEMALE)
                .build();

        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.OGG_OPUS)
                .build();

        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
        return response.getAudioContent().toByteArray();
    }
}
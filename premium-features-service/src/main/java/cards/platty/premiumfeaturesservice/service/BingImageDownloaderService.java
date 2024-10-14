package cards.platty.premiumfeaturesservice.service;

import cards.platty.premiumfeaturesservice.dto.DownloadPremiumImageDtoRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class BingImageDownloaderService implements ImageDownloaderService {

    @Override
    public byte[] getImage(DownloadPremiumImageDtoRequest downloadImageDtoRequest) {
        try {
            Document doc = Jsoup.connect("https://www.bing.com/images/search?q=" +
                    downloadImageDtoRequest.getQuery()).get();
            Elements images = doc.select("img.mimg");

            for (Element img : images) {
                String imageUrl = img.attr("src");
                if (imageUrl.startsWith("https://")) {
                    return downloadImage(imageUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[1024];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                return out.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
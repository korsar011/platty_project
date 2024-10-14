package cards.platty.premiumfeaturesservice.service;

import cards.platty.premiumfeaturesservice.dto.DownloadPremiumImageDtoRequest;

public interface ImageDownloaderService {
    byte[] getImage(DownloadPremiumImageDtoRequest downloadPremiumImageDtoRequest);
}
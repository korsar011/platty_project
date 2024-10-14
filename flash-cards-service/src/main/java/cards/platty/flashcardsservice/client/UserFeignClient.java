package cards.platty.flashcardsservice.client;

import cards.platty.flashcardsservice.dto.ImageUrlDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PutMapping("/api/users/{id}/profile-image")
    void updateUserProfileImage(@PathVariable("id") Long userId, @RequestBody ImageUrlDto imageUrlDto);
}
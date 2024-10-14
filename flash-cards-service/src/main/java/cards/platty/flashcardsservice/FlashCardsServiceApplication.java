package cards.platty.flashcardsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FlashCardsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashCardsServiceApplication.class, args);
    }

}

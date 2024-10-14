//package cards.platty.premiumfeaturesservice.config;
//
//import feign.RequestInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FeignConfig {
//
//    @Bean
//    public RequestInterceptor requestInterceptor(JwtTokenProvider jwtTokenProvider) {
//        return requestTemplate -> {
//            String token = jwtTokenProvider.getToken(); // Метод для получения текущего JWT токена
//            if (token != null) {
//                requestTemplate.header("Authorization", "Bearer " + token);
//            }
//        };
//    }
//}
package cards.platty.authservice.dto;

import lombok.Data;


@Data
public class UserRegistrationResponse {

    private String message;

    private Long userId;

    private String username;

    private String email;


    public UserRegistrationResponse(String message, Long userId, String username, String email) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

}

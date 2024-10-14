package cards.platty.authservice.dto;

import cards.platty.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String username;

    private String password;

    public User toEntity() {
        return new User(null, this.username, this.password);
    }
}

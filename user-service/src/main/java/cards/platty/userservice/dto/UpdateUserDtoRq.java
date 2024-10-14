package cards.platty.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateUserDtoRq {
    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate birthday;

    private String country;

    private List<String> languages;
}

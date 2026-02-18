package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private Long roleId; // role master id
}

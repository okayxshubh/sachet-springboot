package in.gov.cybercrime.sachet.dto;

import in.gov.cybercrime.sachet.masters.RoleMaster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private RoleMaster role;
}

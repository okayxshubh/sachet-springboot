package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String name;
    private String rank;
    private String psName;
    private String district;
    private String phone;
    private String role;
    private String password;
    private String updatedBy;
}

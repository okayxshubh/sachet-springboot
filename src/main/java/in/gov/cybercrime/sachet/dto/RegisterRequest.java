package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String name;
    private String phone;
    private String password;

    private Long rankId;
    private Long psId;
    private Long districtId;
    private Long roleId;
}

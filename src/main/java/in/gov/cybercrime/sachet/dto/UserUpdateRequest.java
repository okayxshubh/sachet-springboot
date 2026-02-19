package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String name;
    private Long rankId;
    private Long psId;
    private Long roleId;
    private String phone;
    private String password;
    private Boolean isActive;
    private Boolean enabled;
    private String updatedBy;
}

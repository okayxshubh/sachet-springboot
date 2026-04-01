package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveUserRequest {
    private Long id;
    private String name;
    private Long roleId;
    private Long rankId;
    private Long psId;
    private String phone;
    private Boolean isActive;
    private String updatedBy;
}
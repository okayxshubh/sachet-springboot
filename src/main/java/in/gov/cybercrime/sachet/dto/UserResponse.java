package in.gov.cybercrime.sachet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String rankName;
    private String psName;
    private String districtName;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private Boolean isApproved;
}

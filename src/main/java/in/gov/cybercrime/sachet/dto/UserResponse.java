package in.gov.cybercrime.sachet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String rank;
    private String psName;
    private String district;
    private String phone;
    private String role;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}

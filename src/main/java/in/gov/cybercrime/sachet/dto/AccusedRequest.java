package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccusedRequest {
    private Long id;
    private Long caseId;
    private String name;
    private String fatherName;
    private String address;
    private Boolean arrested;
    private String updatedBy;
}

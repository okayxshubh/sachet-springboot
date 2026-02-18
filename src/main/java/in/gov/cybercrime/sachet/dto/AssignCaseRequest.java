package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignCaseRequest {
    private Long caseId;
    private Long assignedToId;
    private String updatedBy;
}

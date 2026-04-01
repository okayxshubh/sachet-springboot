package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaseUpdateRequest {

    private String firNo;
    private Integer firYear;

    // Use IDs instead of names
    private Long psId;
    private Long districtId;

    private String sections;
    private String summary;
    private Long createdById;
    private String updatedBy;

    // New fields
    private Long caseStatusId;
    private List<Long> assignedToIds;
}
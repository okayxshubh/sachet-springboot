package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaseCreateRequest {

    private String firNo;
    private Integer firYear;

    // Use IDs instead of names
    private Long psId;
    private Long districtId;

    private String sections;
    private String summary;
    private Long createdById;

    // New fields
    private Long caseStatusId;
    private List<Long> assignedToIds;
}
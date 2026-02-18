package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseCreateRequest {
    private String firNo;
    private Integer firYear;
    private String psName;
    private String district;
    private String sections;
    private String summary;
    private Long createdById;
    private Long assignedToId;
}

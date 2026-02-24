package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseListRequest {
    private String firNo;
    private Integer firYear;
    private Long assignedToId;
    private Boolean isActive;
    private String monthYear;
}

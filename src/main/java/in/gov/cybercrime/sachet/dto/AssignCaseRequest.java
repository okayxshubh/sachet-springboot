package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignCaseRequest {

    private List<Long> assignedToIds;
    private String updatedBy;
}
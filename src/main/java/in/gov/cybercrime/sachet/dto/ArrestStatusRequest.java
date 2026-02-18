package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrestStatusRequest {
    private Boolean arrested;
    private String updatedBy;
}

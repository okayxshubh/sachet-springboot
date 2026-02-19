package in.gov.cybercrime.sachet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PSResponse {
    private Long id;
    private String psName;
    private DistrictResponse district;
}
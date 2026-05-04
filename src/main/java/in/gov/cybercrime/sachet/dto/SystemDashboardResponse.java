package in.gov.cybercrime.sachet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemDashboardResponse {
    private Integer activeCases;
    private Integer totalOfficers;
    private Double responseRate;
}
package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeleteCaseRequest {
    private Long id;
    private String updatedBy;
}
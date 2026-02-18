package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CorrespondenceRequest {
    private String type;
    private String subject;
    private LocalDate dateSent;
    private String replySummary;
}

package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CaseDiaryRequest {
    private LocalDate diaryDate;
    private String content;
    private Integer version;
    private String updatedBy;
}

package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NoticeRequest {
    private String noticeId;
    private String noticeType;
    private String issuedTo;
    private LocalDate issuedDate;
    private String status;
    private String updatedBy;
}

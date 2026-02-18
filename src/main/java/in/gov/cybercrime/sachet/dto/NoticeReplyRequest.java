package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NoticeReplyRequest {
    private Long id;
    private Long noticeId;
    private LocalDate replyDate;
    private String summary;
    private String status;
    private String updatedBy;
}

package in.gov.cybercrime.sachet.dto;

import in.gov.cybercrime.sachet.entity.NoticeDispatch;
import in.gov.cybercrime.sachet.entity.NoticeReply;
import in.gov.cybercrime.sachet.entity.enums.NoticeLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {

    private Long id;
    private Long caseId;

    private String noticeId;
    private String noticeType;

    private NoticeLayer layer;

    private NoticeDispatch dispatch;
    private NoticeReply reply;

    private String issuedTo;
    private LocalDate issuedDate;

    private String remarks;
    private LocalDate replyDate;

    private DocumentInfo document;
}
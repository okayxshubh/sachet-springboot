package in.gov.cybercrime.sachet.dto;

import lombok.Data;

@Data
public class NoticeTemplateRequest {
    private Long id;
    private Long noticeTypeId;
    private String content;
}

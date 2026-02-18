package in.gov.cybercrime.sachet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeTransactionDeleteRequest {
    private Long noticeId;
    private Long transactionId;
}

package in.gov.cybercrime.sachet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDashboardResponse {
    private Long caseId;
    private String caseNo;
    private String firNo;
    private Integer firYear;
    private String policeStation;
    private String district;
    private String status;
    private String caseSummary;
    private String caseOwner;
    private Integer noticeCount;
    private Integer sentNoticeCount;
    private Integer repliedNoticeCount;
    private Integer pendingNoticeCount;
    private Integer dispatchDocumentCount;
    private Integer replyDocumentCount;
    private Integer diaryEntryCount;
    private Integer assignedOfficerCount;
}

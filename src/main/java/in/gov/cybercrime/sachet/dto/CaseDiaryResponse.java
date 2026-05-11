package in.gov.cybercrime.sachet.dto;

import in.gov.cybercrime.sachet.entity.enums.CaseDiaryEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDiaryResponse {

    private Long id;
    private Long caseId;
    private String firNo;
    private Long noticeDbId;
    private String noticeId;
    private CaseDiaryEventType eventType;
    private String summary;
    private Long performedById;
    private String performedByName;
    private String performedByRank;
    private LocalDateTime eventTime;
    private Integer versionNo;
    private String metaData;
}

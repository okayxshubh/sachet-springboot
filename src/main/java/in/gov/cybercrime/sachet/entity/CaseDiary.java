package in.gov.cybercrime.sachet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.gov.cybercrime.sachet.entity.enums.CaseDiaryEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_diaries")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CaseDiary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    /**
     * Event Type
     * NOTICE_CREATED
     * NOTICE_SENT
     * NOTICE_REPLIED
     * etc
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private CaseDiaryEventType eventType;

    /**
     * Human readable diary text
     */
    @Column(name = "summary", nullable = false, length = 5000)
    private String summary;

    /**
     * Reference Notice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    /**
     * Who performed action
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    /**
     * Exact event timestamp
     */
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime = LocalDateTime.now();

    /**
     * Soft ordering/versioning
     */
    @Column(name = "version_no")
    private Integer versionNo = 1;

    /**
     * Optional metadata JSON
     */
    @Column(name = "meta_data", columnDefinition = "TEXT")
    private String metaData;
}

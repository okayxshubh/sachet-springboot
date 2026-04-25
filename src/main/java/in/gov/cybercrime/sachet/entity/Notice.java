package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.entity.enums.NoticeLayer;
import in.gov.cybercrime.sachet.masters.NoticeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "notices")
@Getter
@Setter
public class Notice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "notice_id", nullable = false, unique = true)
    private String noticeId;

    @Column(name = "notice_type", nullable = false)
    private String noticeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "layer", nullable = false)
    private NoticeLayer layer = NoticeLayer.LAYER_1;

    // Sent File Pojo
    @Embedded
    private NoticeDispatch dispatch = new NoticeDispatch();

    // Reply File Pojo
    @Embedded
    private NoticeReply reply = new NoticeReply();

    @Enumerated(EnumType.STRING)
    private NoticeStatus status = NoticeStatus.PENDING;
}
package in.gov.cybercrime.sachet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "issued_to", nullable = false)
    private String issuedTo;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "status")
    private String status = "Pending";
}

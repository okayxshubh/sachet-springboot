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
@Table(name = "correspondence")
@Getter
@Setter
public class Correspondence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "date_sent", nullable = false)
    private LocalDate dateSent;

    @Column(name = "reply_summary")
    private String replySummary;
}

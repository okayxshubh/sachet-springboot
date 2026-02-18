package in.gov.cybercrime.sachet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cases")
@Getter
@Setter
public class CaseFile extends BaseEntity {

    @Column(name = "fir_no", nullable = false)
    private String firNo;

    @Column(name = "fir_year", nullable = false)
    private Integer firYear;

    @Column(name = "ps_name", nullable = false)
    private String psName;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "sections")
    private String sections;

    @Column(name = "summary", nullable = false)
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user", nullable = false)
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user", nullable = false)
    private User assignedToUser;
}

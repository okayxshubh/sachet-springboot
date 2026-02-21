package in.gov.cybercrime.sachet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cases")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CaseFile extends BaseEntity {

    @Column(name = "fir_no", nullable = false, unique = true)
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
    @JoinColumn(name = "case_owner", nullable = false)
    private User caseOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user", nullable = false)
    private User assignedToUser;
}
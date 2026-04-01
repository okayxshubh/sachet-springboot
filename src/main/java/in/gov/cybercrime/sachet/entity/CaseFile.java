package in.gov.cybercrime.sachet.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.gov.cybercrime.sachet.masters.CaseStatusMaster;
import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ps_id", nullable = false)
    private PoliceStationMaster policeStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id", nullable = false)
    private DistrictMaster district;

    @Column(name = "sections")
    private String sections;

    @Column(name = "summary", nullable = false)
    private String summary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "case_status", nullable = false)
    private CaseStatusMaster caseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_owner", nullable = false)
    private User caseOwner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "case_assigned_users",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedToUsers = new HashSet<>();
}
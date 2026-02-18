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
@Table(name = "accused")
@Getter
@Setter
public class Accused extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseFile caseFile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "address")
    private String address;

    @Column(name = "arrested")
    private Boolean arrested = false;
}

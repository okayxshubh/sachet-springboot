package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.masters.DocTypeMaster;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doc_templates")
@Getter
@Setter
public class DocTemplate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_type_id", nullable = false, unique = true)
    private DocTypeMaster docType;

    @Column(name = "file_path", nullable = false, length = 1024)
    private String filePath;
}

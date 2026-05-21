package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notice_templates")
@Getter
@Setter
public class NoticeTemplate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_type_id", nullable = false, unique = true)
    private NoticeTypeMaster noticeType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "template_content", nullable = false, columnDefinition = "TEXT")
    private String content;
}

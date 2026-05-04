package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class NoticeDispatch {

    @Column(name = "issued_to", length = 512)
    private String issuedTo;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "fileName", column = @Column(name = "notice_file_name", length = 512)),
        @AttributeOverride(name = "filePath", column = @Column(name = "notice_file_path", length = 1024))
    })
    private DocumentInfo document;
}

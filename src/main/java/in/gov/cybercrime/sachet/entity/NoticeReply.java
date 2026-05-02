package in.gov.cybercrime.sachet.entity;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class NoticeReply {

    @Column(name = "reply_date")
    private LocalDate replyDate;

    @Column(name = "remarks")
    private String remarks;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "fileName", column = @Column(name = "reply_file_name")),
        @AttributeOverride(name = "filePath", column = @Column(name = "reply_file_path"))
    })
    private DocumentInfo document;
}
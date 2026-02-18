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
@Table(name = "notice_replies")
@Getter
@Setter
public class NoticeReply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(name = "reply_date", nullable = false)
    private LocalDate replyDate;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "status")
    private String status;
}

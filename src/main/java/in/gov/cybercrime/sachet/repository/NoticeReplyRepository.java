package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.NoticeReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeReplyRepository extends JpaRepository<NoticeReply, Long> {
    List<NoticeReply> findByNoticeIdAndIsActiveTrue(Long noticeId);
}

package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByNoticeId(String noticeId);

    List<Notice> findByCaseFileId(Long caseId);

}
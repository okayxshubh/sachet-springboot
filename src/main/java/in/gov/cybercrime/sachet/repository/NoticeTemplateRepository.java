package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.NoticeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeTemplateRepository extends JpaRepository<NoticeTemplate, Long> {

    Optional<NoticeTemplate> findByNoticeType_Id(Long noticeTypeId);
}

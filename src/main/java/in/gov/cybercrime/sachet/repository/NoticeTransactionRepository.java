package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.NoticeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeTransactionRepository extends JpaRepository<NoticeTransaction, Long> {
    List<NoticeTransaction> findByNoticeIdAndIsActiveTrue(Long noticeId);
    boolean existsByNoticeIdAndTransactionId(Long noticeId, Long transactionId);
}

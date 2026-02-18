package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeTransactionRequest;
import in.gov.cybercrime.sachet.entity.NcrpTransaction;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.entity.NoticeTransaction;
import in.gov.cybercrime.sachet.repository.NcrpTransactionRepository;
import in.gov.cybercrime.sachet.repository.NoticeRepository;
import in.gov.cybercrime.sachet.repository.NoticeTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeTransactionService {

    private final NoticeTransactionRepository noticeTransactionRepository;
    private final NoticeRepository noticeRepository;
    private final NcrpTransactionRepository transactionRepository;

    public NoticeTransactionService(NoticeTransactionRepository noticeTransactionRepository,
                                    NoticeRepository noticeRepository,
                                    NcrpTransactionRepository transactionRepository) {
        this.noticeTransactionRepository = noticeTransactionRepository;
        this.noticeRepository = noticeRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<NoticeTransaction> listByNotice(Long noticeId) {
        return noticeTransactionRepository.findByNoticeIdAndIsActiveTrue(noticeId);
    }

    public NoticeTransaction create(Long noticeId, NoticeTransactionRequest request) {
        if (noticeTransactionRepository.existsByNoticeIdAndTransactionId(noticeId, request.getTransactionId())) {
            throw new RuntimeException("Mapping already exists");
        }

        Notice notice = getNotice(noticeId);
        NcrpTransaction transaction = getTransaction(request.getTransactionId());

        NoticeTransaction mapping = new NoticeTransaction();
        mapping.setNotice(notice);
        mapping.setTransaction(transaction);
        return noticeTransactionRepository.save(mapping);
    }

    public void delete(Long noticeId, Long transactionId) {
        List<NoticeTransaction> mappings = noticeTransactionRepository.findByNoticeIdAndIsActiveTrue(noticeId);
        for (NoticeTransaction mapping : mappings) {
            if (mapping.getTransaction().getId().equals(transactionId)) {
                mapping.setIsActive(false);
                noticeTransactionRepository.save(mapping);
                return;
            }
        }
        throw new RuntimeException("Mapping not found");
    }

    private Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    private NcrpTransaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}

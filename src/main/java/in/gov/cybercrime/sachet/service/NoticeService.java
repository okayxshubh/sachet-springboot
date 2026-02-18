package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CaseFileRepository caseFileRepository;

    public NoticeService(NoticeRepository noticeRepository, CaseFileRepository caseFileRepository) {
        this.noticeRepository = noticeRepository;
        this.caseFileRepository = caseFileRepository;
    }

    public List<Notice> listByCase(Long caseId) {
        return noticeRepository.findByCaseFileIdAndIsActiveTrue(caseId);
    }

    public Notice create(Long caseId, NoticeRequest request) {
        CaseFile caseFile = getCase(caseId);
        Notice notice = new Notice();
        notice.setCaseFile(caseFile);
        notice.setNoticeId(request.getNoticeId());
        notice.setNoticeType(request.getNoticeType());
        notice.setIssuedTo(request.getIssuedTo());
        notice.setIssuedDate(request.getIssuedDate());
        if (request.getStatus() != null) notice.setStatus(request.getStatus());
        return noticeRepository.save(notice);
    }

    public Notice update(Long id, NoticeRequest request) {
        Notice notice = getNotice(id);
        if (request.getNoticeId() != null) notice.setNoticeId(request.getNoticeId());
        if (request.getNoticeType() != null) notice.setNoticeType(request.getNoticeType());
        if (request.getIssuedTo() != null) notice.setIssuedTo(request.getIssuedTo());
        if (request.getIssuedDate() != null) notice.setIssuedDate(request.getIssuedDate());
        if (request.getStatus() != null) notice.setStatus(request.getStatus());
        if (request.getUpdatedBy() != null) notice.setUpdatedBy(request.getUpdatedBy());
        return noticeRepository.save(notice);
    }

    private Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    private CaseFile getCase(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }
}

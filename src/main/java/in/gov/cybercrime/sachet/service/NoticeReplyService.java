package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeReplyRequest;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.entity.NoticeReply;
import in.gov.cybercrime.sachet.repository.NoticeReplyRepository;
import in.gov.cybercrime.sachet.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeReplyService {

    private final NoticeReplyRepository noticeReplyRepository;
    private final NoticeRepository noticeRepository;

    public NoticeReplyService(NoticeReplyRepository noticeReplyRepository, NoticeRepository noticeRepository) {
        this.noticeReplyRepository = noticeReplyRepository;
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeReply> listByNotice(Long noticeId) {
        return noticeReplyRepository.findByNoticeIdAndIsActiveTrue(noticeId);
    }

    public NoticeReply create(Long noticeId, NoticeReplyRequest request) {
        Notice notice = getNotice(noticeId);
        NoticeReply reply = new NoticeReply();
        reply.setNotice(notice);
        reply.setReplyDate(request.getReplyDate());
        reply.setSummary(request.getSummary());
        reply.setStatus(request.getStatus());
        return noticeReplyRepository.save(reply);
    }

    public NoticeReply update(Long id, NoticeReplyRequest request) {
        NoticeReply reply = getReply(id);
        if (request.getReplyDate() != null) reply.setReplyDate(request.getReplyDate());
        if (request.getSummary() != null) reply.setSummary(request.getSummary());
        if (request.getStatus() != null) reply.setStatus(request.getStatus());
        return noticeReplyRepository.save(reply);
    }

    private NoticeReply getReply(Long id) {
        return noticeReplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reply not found"));
    }

    private Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }
}

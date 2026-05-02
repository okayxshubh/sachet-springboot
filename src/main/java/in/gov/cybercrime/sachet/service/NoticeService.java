package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.entity.NoticeDispatch;
import in.gov.cybercrime.sachet.entity.NoticeReply;
import in.gov.cybercrime.sachet.entity.enums.NoticeLayer;
import in.gov.cybercrime.sachet.masters.NoticeStatus;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CaseFileRepository caseFileRepository;

    public List<Notice> listByCaseWithOptionalLayer(Long caseId, NoticeLayer layer) {

        if (layer == null) {
            return noticeRepository.findByCaseFileId(caseId);
        }

        return noticeRepository.findByCaseFileIdAndLayer(caseId, layer);
    }

    public Notice create(Long caseId, NoticeRequest request) {

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        Notice notice = new Notice();

        notice.setCaseFile(caseFile);
        notice.setNoticeId(request.getNoticeId());
        notice.setNoticeType(request.getNoticeType());
        notice.setLayer(
                request.getLayer() != null
                        ? request.getLayer()
                        : NoticeLayer.LAYER_1
        );
        notice.setDispatch(request.getDispatch());
        notice.setReply(request.getReply());
        notice.setStatus(NoticeStatus.PENDING);

        return noticeRepository.save(notice);
    }

    public Notice getById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public Notice update(Long id, NoticeRequest request) {

        Notice notice = getById(id);

        notice.setNoticeId(request.getNoticeId());
        notice.setNoticeType(request.getNoticeType());

        if (request.getLayer() != null) {
            notice.setLayer(request.getLayer());
        }

        notice.setDispatch(request.getDispatch());
        notice.setReply(request.getReply());

        return noticeRepository.save(notice);
    }

    public Notice markSent(Long id) {

        Notice notice = getById(id);

        notice.setStatus(NoticeStatus.SENT);

        if (notice.getDispatch() != null) {
            notice.getDispatch().setIssuedDate(LocalDate.now());
        }

        return noticeRepository.save(notice);
    }

    public Notice markReplied(Long id) {

        Notice notice = getById(id);

        notice.setStatus(NoticeStatus.REPLIED);

        if (notice.getReply() != null) {
            notice.getReply().setReplyDate(LocalDate.now());
        }

        return noticeRepository.save(notice);
    }

    public void delete(Long id) {

        Notice notice = getById(id);
        noticeRepository.delete(notice);
    }

    public Notice getByNoticeId(String noticeId) {
        return noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public List<Notice> getByCaseId(Long caseId) {
        return noticeRepository.findByCaseFileId(caseId);
    }

    public List<Notice> listByCase(Long caseId) {
        return noticeRepository.findByCaseFileId(caseId);
    }

    public List<Notice> listByCaseAndLayer(Long caseId, NoticeLayer layer) {
        return noticeRepository.findByCaseFileIdAndLayer(caseId, layer);
    }

//    HELPER METHODS
public Notice attachDispatchDocument(
        Long id,
        String issuedTo,
        LocalDate issuedDate,
        DocumentInfo document
) {

    Notice notice = getById(id);

    NoticeDispatch dispatch = notice.getDispatch();

    if (dispatch == null) {
        dispatch = new NoticeDispatch();
    }

    dispatch.setIssuedTo(issuedTo);

    dispatch.setIssuedDate(
            issuedDate != null
                    ? issuedDate
                    : LocalDate.now()
    );

    dispatch.setDocument(document);

    notice.setDispatch(dispatch);

    return noticeRepository.save(notice);
}

    public Notice attachReplyDocument(
            Long id,
            LocalDate replyDate,
            String remarks,
            DocumentInfo document
    ) {

        Notice notice = getById(id);

        NoticeReply reply = notice.getReply();

        if (reply == null) {
            reply = new NoticeReply();
        }

        reply.setReplyDate(
                replyDate != null
                        ? replyDate
                        : LocalDate.now()
        );

        reply.setRemarks(remarks);
        reply.setDocument(document);

        notice.setReply(reply);

        return noticeRepository.save(notice);
    }
}
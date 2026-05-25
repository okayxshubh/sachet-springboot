package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.entity.NoticeDispatch;
import in.gov.cybercrime.sachet.entity.NoticeReply;
import in.gov.cybercrime.sachet.entity.User;
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

    // NEW
    private final CaseDiaryService caseDiaryService;

    public List<Notice> listByCaseWithOptionalLayer(Long caseId, NoticeLayer layer) {

        if (layer == null) {
            return noticeRepository.findByCaseFileIdOrderByCreatedAtDescIdDesc(caseId);
        }

        return noticeRepository.findByCaseFileIdAndLayerOrderByCreatedAtDescIdDesc(caseId, layer);
    }

    public Notice create(
            Long caseId,
            NoticeRequest request
    ) {
        return create(caseId, request, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice create(
            Long caseId,
            NoticeRequest request,
            User currentUser
    ) {

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

        Notice saved = noticeRepository.save(notice);

        // AUTO CASE DIARY
        caseDiaryService.logNoticeCreated(
                saved,
                currentUser
        );

        return saved;
    }

    public Notice getById(Long id) {

        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public Notice update(
            Long id,
            NoticeRequest request
    ) {
        return update(id, request, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice update(
            Long id,
            NoticeRequest request,
            User currentUser
    ) {

        Notice notice = getById(id);

        NoticeLayer oldLayer = notice.getLayer();

        notice.setNoticeId(request.getNoticeId());
        notice.setNoticeType(request.getNoticeType());

        if (request.getLayer() != null) {
            notice.setLayer(request.getLayer());
        }

        notice.setDispatch(request.getDispatch());
        notice.setReply(request.getReply());

        Notice saved = noticeRepository.save(notice);

        // LOG LAYER ESCALATION
        if (oldLayer != saved.getLayer()) {

            caseDiaryService.logLayerEscalation(
                    saved,
                    currentUser
            );
        }

        return saved;
    }

    public Notice markSent(
            Long id
    ) {
        return markSent(id, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice markSent(
            Long id,
            User currentUser
    ) {

        Notice notice = getById(id);

        notice.setStatus(NoticeStatus.SENT);

        if (notice.getDispatch() != null) {

            notice.getDispatch().setIssuedDate(
                    LocalDate.now()
            );
        }

        Notice saved = noticeRepository.save(notice);

        // AUTO CASE DIARY
        caseDiaryService.logNoticeSent(
                saved,
                currentUser
        );

        return saved;
    }

    public Notice markReplied(
            Long id
    ) {
        return markReplied(id, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice markReplied(
            Long id,
            User currentUser
    ) {

        Notice notice = getById(id);

        notice.setStatus(NoticeStatus.REPLIED);

        if (notice.getReply() != null) {

            notice.getReply().setReplyDate(
                    LocalDate.now()
            );
        }

        Notice saved = noticeRepository.save(notice);

        // AUTO CASE DIARY
        caseDiaryService.logNoticeReply(
                saved,
                currentUser
        );

        return saved;
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
        return noticeRepository.findByCaseFileIdOrderByCreatedAtDescIdDesc(caseId);
    }

    public List<Notice> listByCase(Long caseId) {
        return noticeRepository.findByCaseFileIdOrderByCreatedAtDescIdDesc(caseId);
    }

    public List<Notice> listByCaseAndLayer(
            Long caseId,
            NoticeLayer layer
    ) {

        return noticeRepository.findByCaseFileIdAndLayerOrderByCreatedAtDescIdDesc(
                caseId,
                layer
        );
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    public Notice attachDispatchDocument(
            Long id,
            String issuedTo,
            LocalDate issuedDate,
            DocumentInfo document
    ) {
        return attachDispatchDocument(id, issuedTo, issuedDate, document, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice attachDispatchDocument(
            Long id,
            String issuedTo,
            LocalDate issuedDate,
            DocumentInfo document,
            User currentUser
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

        Notice saved = noticeRepository.save(notice);

        // AUTO CASE DIARY
        caseDiaryService.logNoticeSent(
                saved,
                currentUser
        );

        return saved;
    }

    public Notice attachReplyDocument(
            Long id,
            LocalDate replyDate,
            String remarks,
            DocumentInfo document
    ) {
        return attachReplyDocument(id, replyDate, remarks, document, caseDiaryService.getCurrentAuthenticatedUser());
    }

    public Notice attachReplyDocument(
            Long id,
            LocalDate replyDate,
            String remarks,
            DocumentInfo document,
            User currentUser
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

        notice.setStatus(NoticeStatus.REPLIED);

        Notice saved = noticeRepository.save(notice);

        // AUTO CASE DIARY
        caseDiaryService.logNoticeReply(
                saved,
                currentUser
        );

        return saved;
    }
}

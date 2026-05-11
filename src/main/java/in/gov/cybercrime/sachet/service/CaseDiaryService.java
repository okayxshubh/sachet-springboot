package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.CaseDiaryRequest;
import in.gov.cybercrime.sachet.dto.CaseDiaryResponse;
import in.gov.cybercrime.sachet.entity.*;
import in.gov.cybercrime.sachet.entity.enums.CaseDiaryEventType;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.repository.CaseDiaryRepository;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseDiaryService {

    private final CaseDiaryRepository caseDiaryRepository;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;

    public List<CaseDiaryResponse> listByCase(Long caseId) {
        return caseDiaryRepository
                .findByCaseFileIdAndIsActiveTrueOrderByEventTimeDescIdDesc(caseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CaseDiaryResponse create(Long caseId, CaseDiaryRequest request) {
        CaseFile caseFile = getCase(caseId);

        CaseDiary diary = new CaseDiary();
        diary.setCaseFile(caseFile);
        diary.setEventType(CaseDiaryEventType.CASE_UPDATED);
        diary.setSummary(request.getContent());
        diary.setEventTime(
                request.getDiaryDate() != null
                        ? request.getDiaryDate().atStartOfDay()
                        : LocalDateTime.now()
        );
        diary.setVersionNo(request.getVersion() != null ? request.getVersion() : 1);
        diary.setUpdatedBy(request.getUpdatedBy());

        return toResponse(caseDiaryRepository.save(diary));
    }

    public CaseDiaryResponse update(Long id, CaseDiaryRequest request) {
        CaseDiary diary = caseDiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case diary not found"));

        if (request.getContent() != null) {
            diary.setSummary(request.getContent());
        }
        if (request.getDiaryDate() != null) {
            diary.setEventTime(request.getDiaryDate().atStartOfDay());
        }
        if (request.getVersion() != null) {
            diary.setVersionNo(request.getVersion());
        }
        if (request.getUpdatedBy() != null) {
            diary.setUpdatedBy(request.getUpdatedBy());
        }

        return toResponse(caseDiaryRepository.save(diary));
    }

    public void logCaseCreated(CaseFile caseFile, User performedBy) {
        String summary = String.format(
                """
                Case Created:
                FIR No: %s/%s
                Police Station: %s
                District: %s
                Status: %s
                Owner: %s
                Assigned Officers: %s
                """,
                value(caseFile.getFirNo()),
                value(caseFile.getFirYear()),
                caseFile.getPoliceStation() != null ? value(caseFile.getPoliceStation().getPsName()) : "N/A",
                caseFile.getDistrict() != null ? value(caseFile.getDistrict().getDistrictName()) : "N/A",
                caseFile.getCaseStatus() != null ? value(caseFile.getCaseStatus().getStatusName()) : "N/A",
                caseFile.getCaseOwner() != null ? value(caseFile.getCaseOwner().getName()) : "N/A",
                formatUsers(caseFile.getAssignedToUsers())
        );

        save(caseFile, null, performedBy, CaseDiaryEventType.CASE_CREATED, summary, null);
    }

    public void logCaseUpdated(CaseFile caseFile, User performedBy, String updatedBy) {
        String summary = String.format(
                """
                Case Updated:
                FIR No: %s/%s
                Sections: %s
                Status: %s
                Summary: %s
                Updated By: %s
                """,
                value(caseFile.getFirNo()),
                value(caseFile.getFirYear()),
                value(caseFile.getSections()),
                caseFile.getCaseStatus() != null ? value(caseFile.getCaseStatus().getStatusName()) : "N/A",
                value(caseFile.getSummary()),
                value(updatedBy)
        );

        save(caseFile, null, performedBy, CaseDiaryEventType.CASE_UPDATED, summary, null);
    }

    public void logCaseAssigned(CaseFile caseFile, User performedBy, String updatedBy) {
        String summary = String.format(
                """
                Case Assigned:
                FIR No: %s/%s
                Assigned Officers: %s
                Updated By: %s
                """,
                value(caseFile.getFirNo()),
                value(caseFile.getFirYear()),
                formatUsers(caseFile.getAssignedToUsers()),
                value(updatedBy)
        );

        save(caseFile, null, performedBy, CaseDiaryEventType.CASE_ASSIGNED, summary, null);
    }

    public void logCaseDeleted(CaseFile caseFile, User performedBy, String updatedBy) {
        String summary = String.format(
                """
                Case Deleted:
                FIR No: %s/%s
                Updated By: %s
                """,
                value(caseFile.getFirNo()),
                value(caseFile.getFirYear()),
                value(updatedBy)
        );

        save(caseFile, null, performedBy, CaseDiaryEventType.CASE_DELETED, summary, null);
    }

    public void logNoticeCreated(
            Notice notice,
            User performedBy
    ) {

        String summary = String.format(
                """
                Notice Generated:
                Notice ID: %s
                Type: %s
                Layer: %s
                Status: %s
                """,
                notice.getNoticeId(),
                notice.getNoticeType(),
                notice.getLayer(),
                notice.getStatus()
        );

        save(notice, performedBy, CaseDiaryEventType.NOTICE_CREATED, summary);
    }

    public void logNoticeSent(
            Notice notice,
            User performedBy
    ) {

        NoticeDispatch dispatch = notice.getDispatch();

        String summary = String.format(
                """
                Notice Sent:
                Notice ID: %s
                Sent To: %s
                Sent Date: %s
                Layer: %s
                Status: %s
                """,
                notice.getNoticeId(),
                dispatch != null ? dispatch.getIssuedTo() : "N/A",
                dispatch != null ? dispatch.getIssuedDate() : "N/A",
                notice.getLayer(),
                notice.getStatus()
        );

        save(notice, performedBy, CaseDiaryEventType.NOTICE_SENT, summary);
    }

    public void logNoticeReply(
            Notice notice,
            User performedBy
    ) {

        NoticeReply reply = notice.getReply();

        String summary = String.format(
                """
                Notice Reply Received:
                Notice ID: %s
                Reply Date: %s
                Remarks: %s
                Status: %s
                """,
                notice.getNoticeId(),
                reply != null ? reply.getReplyDate() : "N/A",
                reply != null ? reply.getRemarks() : "N/A",
                notice.getStatus()
        );

        save(notice, performedBy, CaseDiaryEventType.NOTICE_REPLIED, summary);
    }

    public void logLayerEscalation(
            Notice notice,
            User performedBy
    ) {

        String summary = String.format(
                """
                Notice Layer Escalated:
                Notice ID: %s
                Current Layer: %s
                Current Status: %s
                """,
                notice.getNoticeId(),
                notice.getLayer(),
                notice.getStatus()
        );

        save(notice, performedBy, CaseDiaryEventType.NOTICE_LAYER_ESCALATED, summary);
    }

    private void save(
            Notice notice,
            User performedBy,
            CaseDiaryEventType eventType,
            String summary
    ) {
        save(notice.getCaseFile(), notice, performedBy, eventType, summary, null);
    }

    private void save(
            CaseFile caseFile,
            Notice notice,
            User performedBy,
            CaseDiaryEventType eventType,
            String summary,
            String metaData
    ) {
        CaseDiary diary = new CaseDiary();

        diary.setCaseFile(caseFile);
        diary.setNotice(notice);
        diary.setPerformedBy(performedBy);
        diary.setEventType(eventType);
        diary.setSummary(summary);
        diary.setEventTime(LocalDateTime.now());
        diary.setVersionNo(1);
        diary.setMetaData(metaData);

        caseDiaryRepository.save(diary);
    }

    public User getUserIfPresent(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByPhoneIfPresent(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return userRepository.findByPhone(phone.trim()).orElse(null);
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return getUserByPhoneIfPresent(authentication.getName());
    }

    public User resolveOfficer(Long userId, String updatedBy) {
        User user = getUserIfPresent(userId);
        if (user != null) {
            return user;
        }

        user = getUserByPhoneIfPresent(updatedBy);
        if (user != null) {
            return user;
        }

        return getCurrentAuthenticatedUser();
    }

    private CaseFile getCase(Long caseId) {
        return caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));
    }

    private CaseDiaryResponse toResponse(CaseDiary diary) {
        CaseFile caseFile = diary.getCaseFile();
        Notice notice = diary.getNotice();
        User performedBy = diary.getPerformedBy();

        return CaseDiaryResponse.builder()
                .id(diary.getId())
                .caseId(caseFile != null ? caseFile.getId() : null)
                .firNo(caseFile != null ? caseFile.getFirNo() : null)
                .noticeDbId(notice != null ? notice.getId() : null)
                .noticeId(notice != null ? notice.getNoticeId() : null)
                .eventType(diary.getEventType())
                .summary(diary.getSummary())
                .performedById(performedBy != null ? performedBy.getId() : null)
                .performedByName(performedBy != null ? performedBy.getName() : null)
                .performedByRank(
                        performedBy != null && performedBy.getRank() != null
                                ? performedBy.getRank().getRankName()
                                : null
                )
                .eventTime(diary.getEventTime())
                .versionNo(diary.getVersionNo())
                .metaData(diary.getMetaData())
                .build();
    }

    private String formatUsers(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return "N/A";
        }
        return users.stream()
                .map(User::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String value(Object value) {
        if (value == null) {
            return "N/A";
        }
        String text = String.valueOf(value).trim();
        return text.isBlank() ? "N/A" : text;
    }
}

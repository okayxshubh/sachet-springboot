package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.CaseDashboardResponse;
import in.gov.cybercrime.sachet.dto.SystemDashboardResponse;
import in.gov.cybercrime.sachet.entity.CaseDiary;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.masters.NoticeStatus;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.NoticeRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CaseService caseService;
    private final NoticeService noticeService;
    private final CaseDiaryService caseDiaryService;
    private final CaseFileRepository caseFileRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public CaseDashboardResponse getCaseDashboard(Long caseId) {
        if (caseId == null) {
            throw new IllegalArgumentException("Case id is required");
        }

        CaseFile caseFile = caseService.getCase(caseId);
        List<Notice> notices = noticeService.getByCaseId(caseId);
        List<CaseDiary> diaries = caseDiaryService.listByCase(caseId);

        int noticeCount = notices.size();
        int sentNoticeCount = (int) notices.stream().filter(n -> n.getStatus() == NoticeStatus.SENT).count();
        int repliedNoticeCount = (int) notices.stream().filter(n -> n.getStatus() == NoticeStatus.REPLIED).count();
        int pendingNoticeCount = (int) notices.stream().filter(n -> n.getStatus() == NoticeStatus.PENDING).count();
        int dispatchDocumentCount = (int) notices.stream().filter(n -> n.getDispatch() != null && n.getDispatch().getDocument() != null).count();
        int replyDocumentCount = (int) notices.stream().filter(n -> n.getReply() != null && n.getReply().getDocument() != null).count();
        int diaryEntryCount = diaries.size();
        int assignedOfficerCount = caseFile.getAssignedToUsers() != null ? caseFile.getAssignedToUsers().size() : 0;

        String caseNo = caseFile.getFirNo();
        if (caseFile.getFirYear() != null) {
            caseNo = caseNo + "/" + caseFile.getFirYear();
        }

        return CaseDashboardResponse.builder()
                .caseId(caseId)
                .caseNo(caseNo)
                .firNo(caseFile.getFirNo())
                .firYear(caseFile.getFirYear())
                .policeStation(caseFile.getPoliceStation() != null ? caseFile.getPoliceStation().getPsName() : null)
                .district(caseFile.getDistrict() != null ? caseFile.getDistrict().getDistrictName() : null)
                .status(caseFile.getCaseStatus() != null ? caseFile.getCaseStatus().getStatusName() : null)
                .caseSummary(caseFile.getSummary())
                .caseOwner(caseFile.getCaseOwner() != null ? caseFile.getCaseOwner().getName() : null)
                .noticeCount(noticeCount)
                .sentNoticeCount(sentNoticeCount)
                .repliedNoticeCount(repliedNoticeCount)
                .pendingNoticeCount(pendingNoticeCount)
                .dispatchDocumentCount(dispatchDocumentCount)
                .replyDocumentCount(replyDocumentCount)
                .diaryEntryCount(diaryEntryCount)
                .assignedOfficerCount(assignedOfficerCount)
                .build();
    }

    public SystemDashboardResponse getSystemDashboard() {
        // Get active cases count
        List<CaseFile> activeCases = caseFileRepository.findAll().stream()
                .filter(c -> c.getIsActive() != null && c.getIsActive())
                .toList();
        int activeCasesCount = activeCases.size();

        // Get total officers count (approved users)
        List<User> officers = userRepository.findAll().stream()
                .filter(u -> u.getIsApproved() != null && u.getIsApproved())
                .toList();
        int totalOfficersCount = officers.size();

        // Calculate response rate (percentage of notices that got replies)
        List<Notice> allNotices = noticeRepository.findAll();
        long totalNotices = allNotices.size();
        long repliedNotices = allNotices.stream()
                .filter(n -> n.getStatus() == NoticeStatus.REPLIED)
                .count();

        double responseRate = totalNotices > 0 ? (double) repliedNotices / totalNotices * 100 : 0.0;

        return SystemDashboardResponse.builder()
                .activeCases(activeCasesCount)
                .totalOfficers(totalOfficersCount)
                .responseRate(Math.round(responseRate * 100.0) / 100.0) // Round to 2 decimal places
                .build();
    }
}

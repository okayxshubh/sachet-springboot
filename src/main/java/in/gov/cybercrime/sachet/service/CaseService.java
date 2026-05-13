package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AssignCaseRequest;
import in.gov.cybercrime.sachet.dto.CaseCreateRequest;
import in.gov.cybercrime.sachet.dto.CaseUpdateRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.masters.CaseStatusMaster;
import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.repository.master_repos.CaseStatusRepository;
import in.gov.cybercrime.sachet.repository.master_repos.DistrictMasterRepository;
import in.gov.cybercrime.sachet.repository.master_repos.PoliceStationMasterRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final CaseStatusRepository caseStatusRepository;
    private final PoliceStationMasterRepository policeStationRepository;
    private final DistrictMasterRepository districtRepository;
    private final CaseDiaryService caseDiaryService;

    public CaseService(CaseFileRepository caseFileRepository,
                       UserRepository userRepository,
                       CaseStatusRepository caseStatusRepository,
                       PoliceStationMasterRepository policeStationRepository,
                       DistrictMasterRepository districtRepository,
                       CaseDiaryService caseDiaryService) {
        this.caseFileRepository = caseFileRepository;
        this.userRepository = userRepository;
        this.caseStatusRepository = caseStatusRepository;
        this.policeStationRepository = policeStationRepository;
        this.districtRepository = districtRepository;
        this.caseDiaryService = caseDiaryService;
    }

    public CaseFile createCase(CaseCreateRequest request) {
        try {
            CaseFile caseFile = new CaseFile();

            caseFile.setFirNo(request.getFirNo());
            caseFile.setFirYear(request.getFirYear());
            caseFile.setPoliceStation(getPoliceStation(request.getPsId()));
            caseFile.setDistrict(getDistrict(request.getDistrictId()));
            caseFile.setSections(request.getSections());
            caseFile.setSummary(request.getSummary());
            caseFile.setCaseOwner(getUser(request.getCreatedById()));

            if (request.getCaseStatusId() != null) {
                caseFile.setCaseStatus(getCaseStatus(request.getCaseStatusId()));
            }

            if (request.getAssignedToIds() != null && !request.getAssignedToIds().isEmpty()) {
                caseFile.setAssignedToUsers(getUsersByIds(request.getAssignedToIds()));
            }

            CaseFile saved = caseFileRepository.save(caseFile);
            caseDiaryService.logCaseCreated(saved, saved.getCaseOwner());

            return saved;

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("FIR number must be unique");
        }
    }

    public CaseFile updateCase(Long id, CaseUpdateRequest request) {
        CaseFile caseFile = getCaseEntity(id);
        String previousFirNo = caseFile.getFirNo();
        Integer previousFirYear = caseFile.getFirYear();
        Long previousPsId = caseFile.getPoliceStation() != null ? caseFile.getPoliceStation().getId() : null;
        Long previousDistrictId = caseFile.getDistrict() != null ? caseFile.getDistrict().getId() : null;
        String previousSections = caseFile.getSections();
        String previousSummary = caseFile.getSummary();
        Long previousOwnerId = caseFile.getCaseOwner() != null ? caseFile.getCaseOwner().getId() : null;
        Long previousStatusId = caseFile.getCaseStatus() != null ? caseFile.getCaseStatus().getId() : null;
        Set<User> previousAssignedToUsers = copyUsers(caseFile.getAssignedToUsers());

        if (request.getFirNo() != null) caseFile.setFirNo(request.getFirNo());
        if (request.getFirYear() != null) caseFile.setFirYear(request.getFirYear());
        if (request.getPsId() != null) caseFile.setPoliceStation(getPoliceStation(request.getPsId()));
        if (request.getDistrictId() != null) caseFile.setDistrict(getDistrict(request.getDistrictId()));
        if (request.getSections() != null) caseFile.setSections(request.getSections());
        if (request.getSummary() != null) caseFile.setSummary(request.getSummary());
        if (request.getCreatedById() != null) caseFile.setCaseOwner(getUser(request.getCreatedById()));

        if (request.getAssignedToIds() != null) {
            caseFile.setAssignedToUsers(getUsersByIds(request.getAssignedToIds()));
        }

        if (request.getCaseStatusId() != null) {
            caseFile.setCaseStatus(getCaseStatus(request.getCaseStatusId()));
        }

        if (request.getUpdatedBy() != null)
            caseFile.setUpdatedBy(request.getUpdatedBy());

        CaseFile saved = caseFileRepository.save(caseFile);
        User performedBy = caseDiaryService.resolveOfficer(request.getCreatedById(), request.getUpdatedBy());
        boolean assignmentChanged = request.getAssignedToIds() != null
                && !sameUserIds(previousAssignedToUsers, saved.getAssignedToUsers());
        boolean caseDetailsChanged =
                !Objects.equals(previousFirNo, saved.getFirNo())
                        || !Objects.equals(previousFirYear, saved.getFirYear())
                        || !Objects.equals(previousPsId, saved.getPoliceStation() != null ? saved.getPoliceStation().getId() : null)
                        || !Objects.equals(previousDistrictId, saved.getDistrict() != null ? saved.getDistrict().getId() : null)
                        || !Objects.equals(previousSections, saved.getSections())
                        || !Objects.equals(previousSummary, saved.getSummary())
                        || !Objects.equals(previousOwnerId, saved.getCaseOwner() != null ? saved.getCaseOwner().getId() : null)
                        || !Objects.equals(previousStatusId, saved.getCaseStatus() != null ? saved.getCaseStatus().getId() : null);

        if (caseDetailsChanged || !assignmentChanged) {
            caseDiaryService.logCaseUpdated(saved, performedBy, request.getUpdatedBy());
        }
        if (assignmentChanged) {
            caseDiaryService.logCaseAssignmentUpdated(
                    saved,
                    performedBy,
                    request.getUpdatedBy(),
                    previousAssignedToUsers,
                    saved.getAssignedToUsers()
            );
        }

        return saved;
    }

    public CaseFile assignCase(Long id, AssignCaseRequest request) {
        CaseFile caseFile = getCaseEntity(id);
        Set<User> previousAssignedToUsers = copyUsers(caseFile.getAssignedToUsers());

        if (request.getAssignedToIds() != null) {
            caseFile.setAssignedToUsers(getUsersByIds(request.getAssignedToIds()));
        }

        if (request.getUpdatedBy() != null)
            caseFile.setUpdatedBy(request.getUpdatedBy());

        CaseFile saved = caseFileRepository.save(caseFile);
        caseDiaryService.logCaseAssignmentUpdated(
                saved,
                caseDiaryService.resolveOfficer(null, request.getUpdatedBy()),
                request.getUpdatedBy(),
                previousAssignedToUsers,
                saved.getAssignedToUsers()
        );

        return saved;
    }

    public List<CaseFile> getCasesByAccess(
            Long districtId,
            Long psId,
            Long userId,
            String rankName) {

        if (districtId == null) {
            throw new IllegalArgumentException("District ID required");
        }

        if (psId == null) {
            throw new IllegalArgumentException("Police Station ID required");
        }

        List<CaseFile> filteredCases = caseFileRepository.findAll()
                .stream()
                .filter(c ->
                        c.getDistrict() != null &&
                                districtId.equals(c.getDistrict().getId()))
                .filter(c ->
                        c.getPoliceStation() != null &&
                                psId.equals(c.getPoliceStation().getId()))
                .filter(c ->
                        c.getIsActive() != null &&
                                c.getIsActive())
                .toList();

        // IO -> only assigned cases
        if (rankName != null &&
                rankName.equalsIgnoreCase("IO (Investigating Officer)")) {

            if (userId == null) {
                throw new IllegalArgumentException("User ID required");
            }

            return filteredCases.stream()
                    .filter(c ->
                            c.getAssignedToUsers() != null &&
                                    c.getAssignedToUsers()
                                            .stream()
                                            .anyMatch(user ->
                                                    userId.equals(user.getId())))
                    .toList();
        }

        // SHO + Others -> all filtered cases
        return filteredCases;
    }



    private CaseFile getCaseEntity(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Set<User> getUsersByIds(List<Long> userIds) {
        Set<User> users = new HashSet<>();
        for (Long id : userIds) {
            users.add(getUser(id));
        }
        return users;
    }

    private Set<User> copyUsers(Set<User> users) {
        return users == null ? new HashSet<>() : new HashSet<>(users);
    }

    private boolean sameUserIds(Set<User> left, Set<User> right) {
        return userIds(left).equals(userIds(right));
    }

    private Set<Long> userIds(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return Set.of();
        }
        return users.stream()
                .map(User::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private CaseStatusMaster getCaseStatus(Long statusId) {
        return caseStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Case Status not found"));
    }

    private PoliceStationMaster getPoliceStation(Long psId) {
        return policeStationRepository.findById(psId)
                .orElseThrow(() -> new ResourceNotFoundException("Police Station not found with id: " + psId));
    }

    private DistrictMaster getDistrict(Long districtId) {
        return districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + districtId));
    }

    public CaseFile getCase(Long id) {
        return getCaseEntity(id);
    }

    public void deleteCase(Long id, String updatedBy) {
        CaseFile caseFile = getCaseEntity(id);
        caseFile.setIsActive(false);
        if (updatedBy != null) {
            caseFile.setUpdatedBy(updatedBy);
        }
        CaseFile saved = caseFileRepository.save(caseFile);
        caseDiaryService.logCaseDeleted(
                saved,
                caseDiaryService.resolveOfficer(null, updatedBy),
                updatedBy
        );
    }
}

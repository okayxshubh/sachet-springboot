package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AssignCaseRequest;
import in.gov.cybercrime.sachet.dto.CaseCreateRequest;
import in.gov.cybercrime.sachet.dto.CaseUpdateRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {

    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;

    public CaseService(CaseFileRepository caseFileRepository,
                       UserRepository userRepository) {
        this.caseFileRepository = caseFileRepository;
        this.userRepository = userRepository;
    }

    public CaseFile createCase(CaseCreateRequest request) {
        try {
            CaseFile caseFile = new CaseFile();

            caseFile.setFirNo(request.getFirNo());
            caseFile.setFirYear(request.getFirYear());
            caseFile.setPsName(request.getPsName());
            caseFile.setDistrict(request.getDistrict());
            caseFile.setSections(request.getSections());
            caseFile.setSummary(request.getSummary());
            caseFile.setCaseOwner(getUser(request.getCreatedById()));
            caseFile.setAssignedToUser(getUser(request.getAssignedToId()));

            return caseFileRepository.save(caseFile);

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("FIR already exists for this year");
        }
    }

    // Get Filtered Cases by ID, Year etc
    public List<CaseFile> getFilteredCases(Optional<String> firNo,
                                   Optional<Integer> firYear,
                                   Optional<Long> assignedToId,
                                   Optional<Boolean> isActive,
                                   Optional<String> monthYear) {

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String validatedMonthYear = monthYear.map(v -> {
            try {
                monthYearFormatter.parse(v);
                return v;
            } catch (Exception ex) {
                throw new IllegalArgumentException("monthYear must be in MM-yyyy format");
            }
        }).orElse(null);

        return caseFileRepository.findAll().stream()
                .filter(c -> firNo.map(v -> v.equals(c.getFirNo())).orElse(true))
                .filter(c -> firYear.map(v -> v.equals(c.getFirYear())).orElse(true))
                .filter(c -> assignedToId
                        .map(v -> c.getAssignedToUser() != null &&
                                v.equals(c.getAssignedToUser().getId()))
                        .orElse(true))
                .filter(c -> isActive.map(v -> v.equals(c.getIsActive())).orElse(true))
                .filter(c -> validatedMonthYear == null
                        || (c.getCreatedAt() != null
                        && validatedMonthYear.equals(
                        monthYearFormatter.format(c.getCreatedAt().atZone(ZoneId.of("Asia/Kolkata")))
                )))
                .toList();
    }

    public CaseFile getCase(Long id) {
        return getCaseEntity(id);
    }

    public CaseFile updateCase(Long id, CaseUpdateRequest request) {

        CaseFile caseFile = getCaseEntity(id);

        if (request.getFirNo() != null) caseFile.setFirNo(request.getFirNo());
        if (request.getFirYear() != null) caseFile.setFirYear(request.getFirYear());
        if (request.getPsName() != null) caseFile.setPsName(request.getPsName());
        if (request.getDistrict() != null) caseFile.setDistrict(request.getDistrict());
        if (request.getSections() != null) caseFile.setSections(request.getSections());
        if (request.getSummary() != null) caseFile.setSummary(request.getSummary());
        if (request.getCreatedById() != null)
            caseFile.setCaseOwner(getUser(request.getCreatedById()));
        if (request.getAssignedToId() != null)
            caseFile.setAssignedToUser(getUser(request.getAssignedToId()));
        if (request.getUpdatedBy() != null)
            caseFile.setUpdatedBy(request.getUpdatedBy());

        return caseFileRepository.save(caseFile);
    }


    public CaseFile assignCase(Long id, AssignCaseRequest request) {

        CaseFile caseFile = getCaseEntity(id);

        caseFile.setAssignedToUser(getUser(request.getAssignedToId()));

        if (request.getUpdatedBy() != null)
            caseFile.setUpdatedBy(request.getUpdatedBy());

        return caseFileRepository.save(caseFile);
    }

    private CaseFile getCaseEntity(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void deleteCase(Long id, String updatedBy) {

        CaseFile caseFile = getCaseEntity(id);

        caseFile.setIsActive(false);

        if (updatedBy != null) {
            caseFile.setUpdatedBy(updatedBy);
        }

        caseFileRepository.save(caseFile);
    }
}

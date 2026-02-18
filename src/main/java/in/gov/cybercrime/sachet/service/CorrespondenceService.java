package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.CorrespondenceRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.Correspondence;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.CorrespondenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final CaseFileRepository caseFileRepository;

    public CorrespondenceService(CorrespondenceRepository correspondenceRepository,
                                 CaseFileRepository caseFileRepository) {
        this.correspondenceRepository = correspondenceRepository;
        this.caseFileRepository = caseFileRepository;
    }

    public List<Correspondence> listByCase(Long caseId) {
        return correspondenceRepository.findByCaseFileIdAndIsActiveTrue(caseId);
    }

    public Correspondence create(Long caseId, CorrespondenceRequest request) {
        CaseFile caseFile = getCase(caseId);
        Correspondence correspondence = new Correspondence();
        correspondence.setCaseFile(caseFile);
        correspondence.setType(request.getType());
        correspondence.setSubject(request.getSubject());
        correspondence.setDateSent(request.getDateSent());
        correspondence.setReplySummary(request.getReplySummary());
        return correspondenceRepository.save(correspondence);
    }

    public Correspondence update(Long id, CorrespondenceRequest request) {
        Correspondence correspondence = getCorrespondence(id);
        if (request.getType() != null) correspondence.setType(request.getType());
        if (request.getSubject() != null) correspondence.setSubject(request.getSubject());
        if (request.getDateSent() != null) correspondence.setDateSent(request.getDateSent());
        if (request.getReplySummary() != null) correspondence.setReplySummary(request.getReplySummary());
        return correspondenceRepository.save(correspondence);
    }

    private Correspondence getCorrespondence(Long id) {
        return correspondenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Correspondence not found"));
    }

    private CaseFile getCase(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }
}

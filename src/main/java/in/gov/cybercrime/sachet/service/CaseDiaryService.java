package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.CaseDiaryRequest;
import in.gov.cybercrime.sachet.entity.CaseDiary;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.repository.CaseDiaryRepository;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseDiaryService {

    private final CaseDiaryRepository caseDiaryRepository;
    private final CaseFileRepository caseFileRepository;

    public CaseDiaryService(CaseDiaryRepository caseDiaryRepository, CaseFileRepository caseFileRepository) {
        this.caseDiaryRepository = caseDiaryRepository;
        this.caseFileRepository = caseFileRepository;
    }

    public List<CaseDiary> listByCase(Long caseId) {
        return caseDiaryRepository.findByCaseFileIdAndIsActiveTrue(caseId);
    }

    public CaseDiary create(Long caseId, CaseDiaryRequest request) {
        CaseFile caseFile = getCase(caseId);
        CaseDiary diary = new CaseDiary();
        diary.setCaseFile(caseFile);
        diary.setDiaryDate(request.getDiaryDate());
        diary.setContent(request.getContent());
        if (request.getVersion() != null) diary.setVersion(request.getVersion());
        return caseDiaryRepository.save(diary);
    }

    public CaseDiary update(Long id, CaseDiaryRequest request) {
        CaseDiary diary = getDiary(id);
        if (request.getDiaryDate() != null) diary.setDiaryDate(request.getDiaryDate());
        if (request.getContent() != null) diary.setContent(request.getContent());
        if (request.getVersion() != null) diary.setVersion(request.getVersion());
        if (request.getUpdatedBy() != null) diary.setUpdatedBy(request.getUpdatedBy());
        return caseDiaryRepository.save(diary);
    }

    private CaseDiary getDiary(Long id) {
        return caseDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
    }

    private CaseFile getCase(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }
}

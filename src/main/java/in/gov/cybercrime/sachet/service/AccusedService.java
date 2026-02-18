package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AccusedRequest;
import in.gov.cybercrime.sachet.dto.ArrestStatusRequest;
import in.gov.cybercrime.sachet.entity.Accused;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.repository.AccusedRepository;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccusedService {

    private final AccusedRepository accusedRepository;
    private final CaseFileRepository caseFileRepository;

    public AccusedService(AccusedRepository accusedRepository, CaseFileRepository caseFileRepository) {
        this.accusedRepository = accusedRepository;
        this.caseFileRepository = caseFileRepository;
    }

    public List<Accused> listByCase(Long caseId) {
        return accusedRepository.findByCaseFileIdAndIsActiveTrue(caseId);
    }

    public Accused create(Long caseId, AccusedRequest request) {
        CaseFile caseFile = getCase(caseId);
        Accused accused = new Accused();
        accused.setCaseFile(caseFile);
        accused.setName(request.getName());
        accused.setFatherName(request.getFatherName());
        accused.setAddress(request.getAddress());
        if (request.getArrested() != null) accused.setArrested(request.getArrested());
        return accusedRepository.save(accused);
    }

    public Accused update(Long id, AccusedRequest request) {
        Accused accused = getAccused(id);
        if (request.getName() != null) accused.setName(request.getName());
        if (request.getFatherName() != null) accused.setFatherName(request.getFatherName());
        if (request.getAddress() != null) accused.setAddress(request.getAddress());
        if (request.getArrested() != null) accused.setArrested(request.getArrested());
        return accusedRepository.save(accused);
    }

    public Accused updateArrested(Long id, ArrestStatusRequest request) {
        Accused accused = getAccused(id);
        accused.setArrested(Boolean.TRUE.equals(request.getArrested()));
        return accusedRepository.save(accused);
    }

    private Accused getAccused(Long id) {
        return accusedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accused not found"));
    }

    private CaseFile getCase(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }
}

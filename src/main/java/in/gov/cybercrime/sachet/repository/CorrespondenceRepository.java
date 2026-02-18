package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.Correspondence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrespondenceRepository extends JpaRepository<Correspondence, Long> {
    List<Correspondence> findByCaseFileIdAndIsActiveTrue(Long caseId);
}

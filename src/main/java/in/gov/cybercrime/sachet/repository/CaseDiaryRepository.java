package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.CaseDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseDiaryRepository extends JpaRepository<CaseDiary, Long> {
    List<CaseDiary> findByCaseFileIdAndIsActiveTrue(Long caseId);
}

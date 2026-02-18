package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.Accused;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccusedRepository extends JpaRepository<Accused, Long> {
    List<Accused> findByCaseFileIdAndIsActiveTrue(Long caseId);
}

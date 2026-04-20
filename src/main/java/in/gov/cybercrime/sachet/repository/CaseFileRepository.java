package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.CaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseFileRepository extends JpaRepository<CaseFile, Long> {
    List<CaseFile> findByIsActiveTrue();
}
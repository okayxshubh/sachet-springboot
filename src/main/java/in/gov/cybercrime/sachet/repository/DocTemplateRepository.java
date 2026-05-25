package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.DocTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocTemplateRepository extends JpaRepository<DocTemplate, Long> {

    Optional<DocTemplate> findByDocType_Id(Long docTypeId);
}

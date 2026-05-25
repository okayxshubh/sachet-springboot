package in.gov.cybercrime.sachet.repository.master_repos;

import in.gov.cybercrime.sachet.masters.DocTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeMasterRepository extends JpaRepository<DocTypeMaster, Long> {
}

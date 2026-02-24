package in.gov.cybercrime.sachet.repository.master_repos;

import in.gov.cybercrime.sachet.masters.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {

    // Returns the role with highest ID (lowest privilege in your model)
    Optional<RoleMaster> findTopByOrderByIdDesc();
}

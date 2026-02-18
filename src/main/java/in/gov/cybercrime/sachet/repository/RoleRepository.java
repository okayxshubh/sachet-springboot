package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.masters.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleMaster, Long> {
}

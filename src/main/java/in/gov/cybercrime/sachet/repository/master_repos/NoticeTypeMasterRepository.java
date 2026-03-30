package in.gov.cybercrime.sachet.repository.master_repos;

import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeTypeMasterRepository extends JpaRepository<NoticeTypeMaster, Long> {
}

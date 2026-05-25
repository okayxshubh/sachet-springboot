package in.gov.cybercrime.sachet.repository.master_repos;

import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliceStationMasterRepository extends JpaRepository<PoliceStationMaster, Long> {

    // Purpose: Fetch all police stations by district id
    List<PoliceStationMaster> findByDistrictIdOrderByIdDesc(Long districtId);
}

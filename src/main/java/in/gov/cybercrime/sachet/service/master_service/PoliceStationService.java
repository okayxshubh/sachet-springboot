package in.gov.cybercrime.sachet.service.master_service;

import in.gov.cybercrime.sachet.dto.DistrictResponse;
import in.gov.cybercrime.sachet.dto.PSResponse;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.repository.master_repos.PoliceStationMasterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoliceStationService {

    private final PoliceStationMasterRepository policeStationRepository;

    public PoliceStationService(PoliceStationMasterRepository policeStationRepository) {
        this.policeStationRepository = policeStationRepository;
    }

    // Purpose: Fetch police stations list by districtId
    public List<PSResponse> getPoliceStationsByDistrict(Long districtId) {

        List<PoliceStationMaster> psList = policeStationRepository.findByDistrictId(districtId);

        return psList.stream().map(ps -> new PSResponse(
                ps.getId(),
                ps.getPsName(),
                new DistrictResponse(
                        ps.getDistrict().getId(),
                        ps.getDistrict().getDistrictName()
                )
        )).collect(Collectors.toList());
    }
}

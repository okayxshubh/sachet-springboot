package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.DistrictIdRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.PSResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.master_service.PoliceStationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
public class PoliceStationMasterController {

    private final PoliceStationService policeStationService;

    public PoliceStationMasterController(PoliceStationService policeStationService) {
        this.policeStationService = policeStationService;
    }

    // Purpose: Fetch police stations using districtId (encrypted raw body)
    @PostMapping("/police-stations/by-district")
    public GenericResponse<List<PSResponse>> getPoliceStationsByDistrict(@RequestBody String encryptedBody) {
        try {
            // Purpose: Decrypt raw encrypted request body
            String decryptedJson = SachetCrypto.decrypt(encryptedBody);

            // Purpose: Convert decrypted JSON into DistrictIdRequest
            ObjectMapper mapper = new ObjectMapper();
            DistrictIdRequest request = mapper.readValue(decryptedJson, DistrictIdRequest.class);

            // Purpose: Fetch PS list using districtId
            List<PSResponse> response = policeStationService.getPoliceStationsByDistrict(request.getDistrictId());

            return GenericResponse.ok("Police stations fetched", response);

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }
}

package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.DistrictIdRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.PSResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.master_service.PoliceStationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/masters/police-stations")
public class PoliceStationMasterController {

    private final PoliceStationService policeStationService;
    private final ObjectMapper objectMapper;

    public PoliceStationMasterController(PoliceStationService policeStationService, ObjectMapper objectMapper) {
        this.policeStationService = policeStationService;
        this.objectMapper = objectMapper;
    }

    // Fetch police stations by district (encrypted request)
    @PostMapping("/by-district")
    public GenericResponse<String> getPoliceStationsByDistrict(@RequestBody String encryptedBody) {
        try {
            // Decrypt incoming JSON
            String decryptedJson = SachetCrypto.decrypt(encryptedBody);

            // Map decrypted JSON to DistrictIdRequest
            DistrictIdRequest request = objectMapper.readValue(decryptedJson, DistrictIdRequest.class);

            // Fetch police stations for the district
            List<PSResponse> psList = policeStationService.getPoliceStationsByDistrict(request.getDistrictId());

            // Serialize and encrypt the list only
            String jsonList = objectMapper.writeValueAsString(psList);
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return outer GenericResponse with encrypted array in data
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Police stations fetched successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error fetching police stations")
                    .data(null)
                    .build();
        }
    }
}

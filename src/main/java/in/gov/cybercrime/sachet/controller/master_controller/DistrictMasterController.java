package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.repository.master_repos.DistrictMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters/districts")
public class DistrictMasterController {

    private final DistrictMasterRepository repo;
    private final ObjectMapper mapper;

    public DistrictMasterController(DistrictMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public GenericResponse<String> getAllDistricts() {
        try {
            // Fetch all districts
            List<DistrictMaster> districts = repo.findAll(Sort.by("id").ascending());

            // Serialize list only
            String jsonList = mapper.writeValueAsString(districts);

            // Encrypt the serialized list
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return GenericResponse with encrypted array in data
            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("Districts Fetched Successfully")
                    .data(encryptedData)   // <--- encrypted array here
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }
}

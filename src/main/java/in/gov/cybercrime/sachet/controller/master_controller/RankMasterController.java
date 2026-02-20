package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.repository.master_repos.RankMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/masters/ranks")
public class RankMasterController {

    private final RankMasterRepository repo;
    private final ObjectMapper mapper;

    public RankMasterController(RankMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public GenericResponse<String> getAllRanks() {
        try {
            // Fetch all ranks
            List<RankMaster> ranks = repo.findAll(Sort.by("id").ascending());

            // Serialize the list only
            String jsonList = mapper.writeValueAsString(ranks);

            // Encrypt the serialized list
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return GenericResponse with encrypted data
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Ranks Fetched Successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error fetching ranks")
                    .data(null)
                    .build();
        }
    }
}

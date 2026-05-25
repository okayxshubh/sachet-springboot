package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import in.gov.cybercrime.sachet.repository.master_repos.RoleMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/masters/roles")
public class RoleMasterController {

    private final RoleMasterRepository repo;
    private final ObjectMapper mapper;

    public RoleMasterController(RoleMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public GenericResponse<String> getAllRoles() {
        try {
            // Fetch all roles
            List<RoleMaster> roles = repo.findAll(Sort.by("id").descending());

            // Serialize only the list of roles
            String jsonList = mapper.writeValueAsString(roles);

            // Encrypt the serialized list
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return GenericResponse with encrypted data
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Roles Fetched Successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error fetching roles")
                    .data(null)
                    .build();
        }
    }
}

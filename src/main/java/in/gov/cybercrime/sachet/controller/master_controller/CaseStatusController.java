package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.CaseStatusMaster;
import in.gov.cybercrime.sachet.repository.master_repos.CaseStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/masters/case-status")
public class CaseStatusController {

    private final CaseStatusRepository repo;
    private final ObjectMapper mapper;

    @GetMapping
    public GenericResponse<String> getAllCaseStatuses() {
        try {
            // Fetch all case statuses
            List<CaseStatusMaster> statuses = repo.findAll(Sort.by("id").ascending());

            // Serialize the list
            String jsonList = mapper.writeValueAsString(statuses);

            // Encrypt the serialized data
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return response
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Case Statuses Fetched Successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error fetching case statuses")
                    .data(null)
                    .build();
        }
    }
}

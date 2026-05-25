package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.DocTypeMaster;
import in.gov.cybercrime.sachet.repository.master_repos.DocTypeMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/masters/doc-types")
public class DocTypeMasterController {

    private final DocTypeMasterRepository repo;
    private final ObjectMapper mapper;

    public DocTypeMasterController(DocTypeMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public GenericResponse<String> getAllDocTypes() {
        try {
            List<DocTypeMaster> docTypes = repo.findAll(Sort.by("id").descending());
            String jsonList = mapper.writeValueAsString(docTypes);
            String encryptedData = SachetCrypto.encrypt(jsonList);

            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("Doc Types Fetched Successfully")
                    .data(encryptedData)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }
}

package in.gov.cybercrime.sachet.controller.master_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import in.gov.cybercrime.sachet.repository.master_repos.DistrictMasterRepository;
import in.gov.cybercrime.sachet.repository.master_repos.NoticeTypeMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/masters/notice-types")
public class NoticeTypeMasterController {

    private final NoticeTypeMasterRepository repo;
    private final ObjectMapper mapper;

    public NoticeTypeMasterController(NoticeTypeMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @GetMapping
    public GenericResponse<String> getAllNoticeTypes() {
        try {
            // Fetch all districts
            List<NoticeTypeMaster> noticeTypes = repo.findAll(Sort.by("id").ascending());

            // Serialize list only
            String jsonList = mapper.writeValueAsString(noticeTypes);

            // Encrypt the serialized list
            String encryptedData = SachetCrypto.encrypt(jsonList);

            // Return GenericResponse with encrypted array in data
            return GenericResponse.<String>builder()
                    .status("OK")
                    .message("Notice Types Fetched Successfully")
                    .data(encryptedData)   // <--- encrypted array here
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }
}

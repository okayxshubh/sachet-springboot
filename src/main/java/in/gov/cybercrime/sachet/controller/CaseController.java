package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.service.CaseService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;
    private final ObjectMapper objectMapper;

    public CaseController(CaseService caseService, ObjectMapper objectMapper) {
        this.caseService = caseService;
        this.objectMapper = objectMapper;
    }

    private GenericResponse<String> success(Object data, String message) throws Exception {
        String responseJson = objectMapper.writeValueAsString(data);
        String encryptedData = SachetCrypto.encrypt(responseJson);

        return GenericResponse.<String>builder()
                .status("OK")
                .message(message)
                .data(encryptedData)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping
    public GenericResponse<String> createCase(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        CaseCreateRequest request = objectMapper.readValue(json, CaseCreateRequest.class);

        CaseFile created = caseService.createCase(request);

        return success(created, "Case created successfully");
    }

    // Get Filtered Cases
    @PostMapping("/get-filtered-cases")
    public GenericResponse<String> getFilteredCases(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        CaseListRequest request = objectMapper.readValue(json, CaseListRequest.class);
        List<CaseFile> cases = caseService.getFilteredCases(
                Optional.ofNullable(request.getFirNo()),
                Optional.ofNullable(request.getFirYear()),
                Optional.ofNullable(request.getAssignedToId()),
                Optional.ofNullable(request.getIsActive()),
                Optional.ofNullable(request.getMonthYear())
        );

        return success(cases, "Success");
    }

    // Get Specific Case By ID
    @PostMapping("/get-case-by-id")
    public GenericResponse<String> getCase(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        IdRequest request = objectMapper.readValue(json, IdRequest.class);

        CaseFile caseFile = caseService.getCase(request.getId());

        return success(caseFile, "Success");
    }

    @PutMapping("/update")
    public GenericResponse<String> updateCase(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        CaseUpdateRequest request = objectMapper.readValue(json, CaseUpdateRequest.class);
        CaseFile updated = caseService.updateCase(request.getId(), request);

        return success(updated, "Case updated successfully");
    }

    @PatchMapping("/assign")
    public GenericResponse<String> assignCase(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        AssignCaseRequest request =
                objectMapper.readValue(json, AssignCaseRequest.class);

        CaseFile updated =
                caseService.assignCase(request.getCaseId(), request);

        return success(updated, "Case assigned successfully");
    }

    @PatchMapping("/delete")
    public GenericResponse<String> deleteCase(@RequestBody String encryptedBody) throws Exception {

        String json = SachetCrypto.decrypt(encryptedBody);

        DeleteCaseRequest request =
                objectMapper.readValue(json, DeleteCaseRequest.class);

        caseService.deleteCase(request.getId(), request.getUpdatedBy());

        return success("Case deleted successfully", "Success");
    }
}

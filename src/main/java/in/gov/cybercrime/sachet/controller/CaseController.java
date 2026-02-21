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

    private GenericResponse<String> error(Exception e) {
        e.printStackTrace();
        return GenericResponse.fail(e.getMessage());
    }

    @PostMapping
    public GenericResponse<String> createCase(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            CaseCreateRequest request =
                    objectMapper.readValue(json, CaseCreateRequest.class);

            CaseFile created = caseService.createCase(request);

            return success(created, "Case created successfully");

        } catch (Exception e) {
            return error(e);
        }
    }

    // Get Cases List By Params
    @PostMapping("/list")
    public GenericResponse<String> listCases(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            CaseListRequest request =
                    objectMapper.readValue(json, CaseListRequest.class);

            List<CaseFile> cases = caseService.getCases(
                    Optional.ofNullable(request.getFirNo()),
                    Optional.ofNullable(request.getFirYear()),
                    Optional.ofNullable(request.getAssignedToId())
            );

            return success(cases, "Success");

        } catch (Exception e) {
            return error(e);
        }
    }

    // Get Case By ID
    @PostMapping("/get")
    public GenericResponse<String> getCase(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            IdRequest request = objectMapper.readValue(json, IdRequest.class);

            CaseFile caseFile = caseService.getCase(request.getId());

            return success(caseFile, "Success");

        } catch (Exception e) {
            return error(e);
        }
    }

    @PutMapping("/update")
    public GenericResponse<String> updateCase(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            CaseUpdateRequest request =
                    objectMapper.readValue(json, CaseUpdateRequest.class);

            CaseFile updated =
                    caseService.updateCase(request.getId(), request);

            return success(updated, "Case updated successfully");

        } catch (Exception e) {
            return error(e);
        }
    }

    @PatchMapping("/assign")
    public GenericResponse<String> assignCase(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            AssignCaseRequest request =
                    objectMapper.readValue(json, AssignCaseRequest.class);

            CaseFile updated =
                    caseService.assignCase(request.getCaseId(), request);

            return success(updated, "Case assigned successfully");

        } catch (Exception e) {
            return error(e);
        }
    }

    @PatchMapping("/delete")
    public GenericResponse<String> deleteCase(@RequestBody String encryptedBody) {
        try {
            String json = SachetCrypto.decrypt(encryptedBody);

            DeleteCaseRequest request =
                    objectMapper.readValue(json, DeleteCaseRequest.class);

            caseService.deleteCase(request.getId(), request.getUpdatedBy());

            return success("Case deleted successfully", "Success");

        } catch (Exception e) {
            return error(e);
        }
    }
}
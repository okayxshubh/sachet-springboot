package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.DocTemplateRequest;
import in.gov.cybercrime.sachet.dto.DocTemplateResponse;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.DocTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/docs/templates")
@RequiredArgsConstructor
public class DocTemplateController {

    private final DocTemplateService docTemplateService;
    private final ObjectMapper objectMapper;

    private GenericResponse<String> success(Object data, String message) throws Exception {
        String responseJson = objectMapper.writeValueAsString(data);
        String encryptedData = SachetCrypto.encrypt(responseJson);

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message(message)
                .data(encryptedData)
                .build();
    }

    @GetMapping
    public GenericResponse<String> list() {
        try {
            List<DocTemplateResponse> templates = docTemplateService.listTemplates();
            return success(templates, "Doc templates fetched successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public GenericResponse<String> detailById(@PathVariable Long id) {
        try {
            return success(
                    docTemplateService.getTemplate(id),
                    "Doc template fetched successfully"
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/detail")
    public GenericResponse<String> detail(@RequestBody DocTemplateRequest request) {
        try {
            DocTemplateResponse response;
            if (request.getId() != null) {
                response = docTemplateService.getTemplate(request.getId());
            } else {
                response = docTemplateService.getTemplateByDocType(request.getDocTypeId());
            }
            return success(response, "Doc template fetched successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public GenericResponse<String> updateById(
            @PathVariable Long id,
            @RequestBody DocTemplateRequest request
    ) {
        try {
            return success(
                    docTemplateService.updateTemplate(id, request.getContent(), request.getContentBase64()),
                    "Doc template updated successfully"
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public GenericResponse<String> update(@RequestBody DocTemplateRequest request) {
        try {
            DocTemplateResponse response = request.getId() != null
                    ? docTemplateService.updateTemplate(request.getId(), request.getContent(), request.getContentBase64())
                    : docTemplateService.updateTemplateByDocType(request.getDocTypeId(), request.getContent(), request.getContentBase64());
            return success(response, "Doc template updated successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }
}

package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeTemplateRequest;
import in.gov.cybercrime.sachet.dto.NoticeTemplateResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.NoticeTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notices/templates")
@RequiredArgsConstructor
public class NoticeTemplateController {

    private final NoticeTemplateService noticeTemplateService;
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
            List<NoticeTemplateResponse> templates = noticeTemplateService.listTemplates();
            return success(templates, "Notice templates fetched successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public GenericResponse<String> detailById(@PathVariable Long id) {
        try {
            return success(
                    noticeTemplateService.getTemplate(id),
                    "Notice template fetched successfully"
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/detail")
    public GenericResponse<String> detail(@RequestBody NoticeTemplateRequest request) {
        try {
            NoticeTemplateResponse response;
            if (request.getId() != null) {
                response = noticeTemplateService.getTemplate(request.getId());
            } else {
                response = noticeTemplateService.getTemplateByNoticeType(request.getNoticeTypeId());
            }
            return success(response, "Notice template fetched successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public GenericResponse<String> updateById(
            @PathVariable Long id,
            @RequestBody NoticeTemplateRequest request
    ) {
        try {
            return success(
                    noticeTemplateService.updateTemplate(id, request.getContent(), request.getContentBase64()),
                    "Notice template updated successfully"
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public GenericResponse<String> update(@RequestBody NoticeTemplateRequest request) {
        try {
            NoticeTemplateResponse response = request.getId() != null
                    ? noticeTemplateService.updateTemplate(request.getId(), request.getContent(), request.getContentBase64())
                    : noticeTemplateService.updateTemplateByNoticeType(request.getNoticeTypeId(), request.getContent(), request.getContentBase64());
            return success(response, "Notice template updated successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }
}

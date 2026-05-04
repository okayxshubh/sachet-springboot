package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.DocumentInfo;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.NoticeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeDocumentController {

    private final NoticeDocumentService noticeDocumentService;
    private final ObjectMapper objectMapper;

    /*
    * DOWNLOAD DISPATCH DOCUMENT
    * */
    private String decryptRequestBody(String encrypted) throws Exception {
        String body = String.valueOf(encrypted == null ? "" : encrypted).trim();

        if (body.startsWith("{") && body.endsWith("}")) {
            JsonNode root = objectMapper.readTree(body);
            if (root.has("payload")) {
                body = root.get("payload").asText();
            } else {
                return body;
            }
        }

        return SachetCrypto.decrypt(body);
    }

    private MediaType resolveMediaType(Path path) {
        try {
            String type = Files.probeContentType(path);
            if (type != null && !type.isBlank()) {
                return MediaType.parseMediaType(type);
            }
        } catch (Exception ignored) {
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String resolveFileName(Resource resource, String explicitName) {
        if (explicitName != null && !explicitName.isBlank()) {
            return explicitName.replaceAll("[\\\"\\r\\n]+", "");
        }

        String fallback = resource.getFilename();
        return fallback != null && !fallback.isBlank() ? fallback : "download.bin";
    }

    private String contentDispositionHeader(String fileName) {
        return "attachment; filename=\"" + fileName.replaceAll("[\\\"\\r\\n]+", "") + "\"";
    }

    @PostMapping("/download-dispatch")
    public ResponseEntity<Resource> downloadDispatchDocument(
            @RequestBody String encrypted
    ) throws Exception {

        String decryptedJson = decryptRequestBody(encrypted);

        NoticeRequest request =
                objectMapper.readValue(
                        decryptedJson,
                        NoticeRequest.class
                );

        Long noticeId = request.getId();

        DocumentInfo documentInfo =
                noticeDocumentService.getDispatchDocumentInfo(noticeId);

        Resource resource =
                noticeDocumentService.getDispatchDocument(noticeId);

        String fileName = resolveFileName(resource, documentInfo.getFileName());
        Path filePath = Paths.get(documentInfo.getFilePath()).toAbsolutePath().normalize();

        return ResponseEntity.ok()
                .contentType(resolveMediaType(filePath))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader(fileName))
                .body(resource);
    }

    /*
    * DOWNLOAD REPLY DOCUMENT
    * */
    @PostMapping("/download-reply")
    public ResponseEntity<Resource> downloadReplyDocument(
            @RequestBody String encrypted
    ) throws Exception {

        String decryptedJson = decryptRequestBody(encrypted);

        NoticeRequest request =
                objectMapper.readValue(
                        decryptedJson,
                        NoticeRequest.class
                );

        Long noticeId = request.getId();

        DocumentInfo documentInfo =
                noticeDocumentService.getReplyDocumentInfo(noticeId);

        Resource resource =
                noticeDocumentService.getReplyDocument(noticeId);

        String fileName = resolveFileName(resource, documentInfo.getFileName());
        Path filePath = Paths.get(documentInfo.getFilePath()).toAbsolutePath().normalize();

        return ResponseEntity.ok()
                .contentType(resolveMediaType(filePath))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader(fileName))
                .body(resource);
    }
}

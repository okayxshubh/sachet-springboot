package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.service.NoticeDocumentService;
import in.gov.cybercrime.sachet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeDocumentController {

    private final NoticeService noticeService;
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

        Notice notice =
                noticeService.getById(noticeId);

        Resource resource =
                noticeDocumentService.getDispatchDocument(noticeId);

        String fileName =
                notice.getDispatch()
                        .getDocument()
                        .getFileName();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
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

        Notice notice =
                noticeService.getById(noticeId);

        Resource resource =
                noticeDocumentService.getReplyDocument(noticeId);

        String fileName =
                notice.getReply()
                        .getDocument()
                        .getFileName();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(resource);
    }
}
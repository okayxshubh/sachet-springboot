package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.NoticeTemplate;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import in.gov.cybercrime.sachet.repository.NoticeTemplateRepository;
import in.gov.cybercrime.sachet.service.NoticeTemplateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NoticeTemplateControllerTest {

    private final NoticeTemplateRepository noticeTemplateRepository = mock(NoticeTemplateRepository.class);
    private final NoticeTemplateService noticeTemplateService = new NoticeTemplateService(noticeTemplateRepository);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NoticeTemplateController noticeTemplateController =
            new NoticeTemplateController(noticeTemplateService, objectMapper);

    @TempDir
    Path tempDir;

    @Test
    void responseJsonPreservesTemplateContentAfterDecryption() throws Exception {
        String content = "Subject: Office Notice\r\n\r\n    Dear Team,\r\n\r\n\tThis is to inform you that...  ";
        NoticeTemplate template = template(content);

        when(noticeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));

        GenericResponse<String> response = noticeTemplateController.detailById(1L);
        String decryptedJson = SachetCrypto.decrypt(response.getData());
        JsonNode contentNode = objectMapper.readTree(decryptedJson).get("content");
        JsonNode contentBase64Node = objectMapper.readTree(decryptedJson).get("contentBase64");

        assertEquals(content, contentNode.asText());
        assertEquals(content, new String(java.util.Base64.getDecoder().decode(contentBase64Node.asText()), StandardCharsets.UTF_8));
    }

    private NoticeTemplate template(String content) throws Exception {
        NoticeTypeMaster noticeType = new NoticeTypeMaster(1L, "Test Notice");
        Path templatePath = Files.writeString(tempDir.resolve("test.txt"), content, StandardCharsets.UTF_8);
        NoticeTemplate template = new NoticeTemplate();
        template.setId(1L);
        template.setNoticeType(noticeType);
        template.setFilePath(templatePath.toString());
        return template;
    }
}

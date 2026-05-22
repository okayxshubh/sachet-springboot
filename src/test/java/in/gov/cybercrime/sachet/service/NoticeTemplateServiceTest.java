package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeTemplateResponse;
import in.gov.cybercrime.sachet.entity.NoticeTemplate;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import in.gov.cybercrime.sachet.repository.NoticeTemplateRepository;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NoticeTemplateServiceTest {

    private final NoticeTemplateRepository noticeTemplateRepository = mock(NoticeTemplateRepository.class);
    private final NoticeTemplateService noticeTemplateService = new NoticeTemplateService(noticeTemplateRepository);

    @TempDir
    Path tempDir;

    @Test
    void returnsTemplateContentWithoutChangingWhitespace() throws Exception {
        String content = "Subject: Office Notice\r\n\r\n\tDear Team,  \r\n\r\n    This is to inform you that...\r\n";
        NoticeTemplate template = template(content);

        when(noticeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));

        NoticeTemplateResponse response = noticeTemplateService.getTemplate(1L);

        assertEquals(content, response.getContent());
        assertEquals(Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8)), response.getContentBase64());
    }

    @Test
    void updatesTemplateContentWithoutChangingWhitespace() throws Exception {
        String content = "  Subject: Office Notice\n\n\tDear Team,\n\n    This is to inform you that...  ";
        NoticeTemplate template = template("old content");

        when(noticeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(noticeTemplateRepository.save(any(NoticeTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoticeTemplateResponse response = noticeTemplateService.updateTemplate(1L, content);

        assertEquals(content, Files.readString(Path.of(template.getFilePath()), StandardCharsets.UTF_8));
        assertEquals(content, response.getContent());
    }

    @Test
    void updatesTemplateContentFromBase64() throws Exception {
        String content = "Subject: Office Notice\r\n\r\n\tDear Team,\r\n";
        NoticeTemplate template = template("old content");

        when(noticeTemplateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(noticeTemplateRepository.save(any(NoticeTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoticeTemplateResponse response = noticeTemplateService.updateTemplate(
                1L,
                null,
                Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8))
        );

        assertEquals(content, Files.readString(Path.of(template.getFilePath()), StandardCharsets.UTF_8));
        assertEquals(content, response.getContent());
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

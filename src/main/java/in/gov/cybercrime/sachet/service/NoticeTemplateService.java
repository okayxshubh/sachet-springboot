package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeTemplateResponse;
import in.gov.cybercrime.sachet.entity.NoticeTemplate;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import in.gov.cybercrime.sachet.repository.NoticeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeTemplateService {

    private final NoticeTemplateRepository noticeTemplateRepository;

    public List<NoticeTemplateResponse> listTemplates() {
        return noticeTemplateRepository.findAll(Sort.by("id").descending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public NoticeTemplateResponse getTemplate(Long id) {
        return toResponse(getTemplateEntity(id));
    }

    public NoticeTemplateResponse getTemplateByNoticeType(Long noticeTypeId) {
        if (noticeTypeId == null) {
            throw new IllegalArgumentException("Notice type id is required");
        }
        return toResponse(noticeTemplateRepository.findByNoticeType_Id(noticeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice template not found for notice type: " + noticeTypeId)));
    }

    public NoticeTemplateResponse updateTemplate(Long id, String content, String contentBase64) {
        byte[] bytes = resolveRequestContent(content, contentBase64);
        if (bytes == null) {
            throw new IllegalArgumentException("Template content is required");
        }

        NoticeTemplate template = getTemplateEntity(id);
        writeTemplateBytes(template, bytes);
        return toResponse(noticeTemplateRepository.save(template));
    }

    public NoticeTemplateResponse updateTemplate(Long id, String content) {
        return updateTemplate(id, content, null);
    }

    public NoticeTemplateResponse updateTemplateByNoticeType(Long noticeTypeId, String content, String contentBase64) {
        if (noticeTypeId == null) {
            throw new IllegalArgumentException("Notice type id is required");
        }
        NoticeTemplate template = noticeTemplateRepository.findByNoticeType_Id(noticeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice template not found for notice type: " + noticeTypeId));
        return updateTemplate(template.getId(), content, contentBase64);
    }

    public NoticeTemplateResponse updateTemplateByNoticeType(Long noticeTypeId, String content) {
        return updateTemplateByNoticeType(noticeTypeId, content, null);
    }

    private NoticeTemplate getTemplateEntity(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Template id is required");
        }
        return noticeTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice template not found: " + id));
    }

    private NoticeTemplateResponse toResponse(NoticeTemplate template) {
        NoticeTypeMaster noticeType = template.getNoticeType();
        byte[] contentBytes = readTemplateBytes(template);
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        Path templatePath = resolveTemplatePath(template);

        return NoticeTemplateResponse.builder()
                .id(template.getId())
                .noticeTypeId(noticeType.getId())
                .noticeTypeName(noticeType.getName())
                .fileName(templatePath.getFileName().toString())
                .filePath(template.getFilePath())
                .content(content)
                .contentBase64(Base64.getEncoder().encodeToString(contentBytes))
                .encoding(StandardCharsets.UTF_8.name())
                .mimeType("text/plain")
                .build();
    }

    private byte[] resolveRequestContent(String content, String contentBase64) {
        if (contentBase64 != null) {
            try {
                return Base64.getDecoder().decode(contentBase64);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Template contentBase64 is invalid");
            }
        }
        return content == null ? null : content.getBytes(StandardCharsets.UTF_8);
    }

    private Path resolveTemplatePath(NoticeTemplate template) {
        String filePath = template.getFilePath();
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalStateException("Template file path is missing for template: " + template.getId());
        }

        Path path = Path.of(filePath);
        if (!path.isAbsolute()) {
            path = Path.of("").toAbsolutePath().resolve(path);
        }
        return path.normalize();
    }

    private byte[] readTemplateBytes(NoticeTemplate template) {
        Path path = resolveTemplatePath(template);
        try {
            return Files.readAllBytes(path);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read notice template file: " + path, ex);
        }
    }

    private void writeTemplateBytes(NoticeTemplate template, byte[] bytes) {
        Path path = resolveTemplatePath(template);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to write notice template file: " + path, ex);
        }
    }
}

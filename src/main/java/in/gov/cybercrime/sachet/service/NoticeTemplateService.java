package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NoticeTemplateResponse;
import in.gov.cybercrime.sachet.entity.NoticeTemplate;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.masters.NoticeTypeMaster;
import in.gov.cybercrime.sachet.repository.NoticeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeTemplateService {

    private final NoticeTemplateRepository noticeTemplateRepository;

    public List<NoticeTemplateResponse> listTemplates() {
        return noticeTemplateRepository.findAll(Sort.by("id").ascending())
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

    public NoticeTemplateResponse updateTemplate(Long id, String content) {
        String normalizedContent = normalizeTemplateContent(content);
        if (!StringUtils.hasText(normalizedContent)) {
            throw new IllegalArgumentException("Template content is required");
        }

        NoticeTemplate template = getTemplateEntity(id);
        template.setContent(normalizedContent);
        return toResponse(noticeTemplateRepository.save(template));
    }

    public NoticeTemplateResponse updateTemplateByNoticeType(Long noticeTypeId, String content) {
        if (noticeTypeId == null) {
            throw new IllegalArgumentException("Notice type id is required");
        }
        NoticeTemplate template = noticeTemplateRepository.findByNoticeType_Id(noticeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice template not found for notice type: " + noticeTypeId));
        return updateTemplate(template.getId(), content);
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
        return NoticeTemplateResponse.builder()
                .id(template.getId())
                .noticeTypeId(noticeType.getId())
                .noticeTypeName(noticeType.getName())
                .fileName(template.getFileName())
                .content(normalizeTemplateContent(template.getContent()))
                .build();
    }

    private String normalizeTemplateContent(String content) {
        if (content == null) {
            return "";
        }

        String normalized = content
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\\r\\n", "\n")
                .replace("\\n", "\n")
                .replace("\\t", "    ")
                .replace("\t", "    ")
                .replace('\u00A0', ' ');

        StringBuilder builder = new StringBuilder();
        int blankLineCount = 0;
        String[] lines = normalized.split("\n", -1);

        for (String line : lines) {
            String cleanedLine = line.stripTrailing();
            boolean blank = cleanedLine.isBlank();

            if (blank) {
                blankLineCount += 1;
                if (blankLineCount > 2) {
                    continue;
                }
            } else {
                blankLineCount = 0;
            }

            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(cleanedLine);
        }

        return builder.toString().strip();
    }
}

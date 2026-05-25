package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.DocTemplateResponse;
import in.gov.cybercrime.sachet.entity.DocTemplate;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.masters.DocTypeMaster;
import in.gov.cybercrime.sachet.repository.DocTemplateRepository;
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
public class DocTemplateService {

    private final DocTemplateRepository docTemplateRepository;

    public List<DocTemplateResponse> listTemplates() {
        return docTemplateRepository.findAll(Sort.by("id").descending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DocTemplateResponse getTemplate(Long id) {
        return toResponse(getTemplateEntity(id));
    }

    public DocTemplateResponse getTemplateByDocType(Long docTypeId) {
        if (docTypeId == null) {
            throw new IllegalArgumentException("Doc type id is required");
        }
        return toResponse(docTemplateRepository.findByDocType_Id(docTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Doc template not found for doc type: " + docTypeId)));
    }

    public DocTemplateResponse updateTemplate(Long id, String content, String contentBase64) {
        byte[] bytes = resolveRequestContent(content, contentBase64);
        if (bytes == null) {
            throw new IllegalArgumentException("Template content is required");
        }

        DocTemplate template = getTemplateEntity(id);
        writeTemplateBytes(template, bytes);
        return toResponse(docTemplateRepository.save(template));
    }

    public DocTemplateResponse updateTemplate(Long id, String content) {
        return updateTemplate(id, content, null);
    }

    public DocTemplateResponse updateTemplateByDocType(Long docTypeId, String content, String contentBase64) {
        if (docTypeId == null) {
            throw new IllegalArgumentException("Doc type id is required");
        }
        DocTemplate template = docTemplateRepository.findByDocType_Id(docTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Doc template not found for doc type: " + docTypeId));
        return updateTemplate(template.getId(), content, contentBase64);
    }

    public DocTemplateResponse updateTemplateByDocType(Long docTypeId, String content) {
        return updateTemplateByDocType(docTypeId, content, null);
    }

    private DocTemplate getTemplateEntity(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Template id is required");
        }
        return docTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doc template not found: " + id));
    }

    private DocTemplateResponse toResponse(DocTemplate template) {
        DocTypeMaster docType = template.getDocType();
        byte[] contentBytes = readTemplateBytes(template);
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        Path templatePath = resolveTemplatePath(template);

        return DocTemplateResponse.builder()
                .id(template.getId())
                .docTypeId(docType.getId())
                .docTypeName(docType.getName())
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

    private Path resolveTemplatePath(DocTemplate template) {
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

    private byte[] readTemplateBytes(DocTemplate template) {
        Path path = resolveTemplatePath(template);
        try {
            return Files.readAllBytes(path);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read doc template file: " + path, ex);
        }
    }

    private void writeTemplateBytes(DocTemplate template, byte[] bytes) {
        Path path = resolveTemplatePath(template);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to write doc template file: " + path, ex);
        }
    }
}

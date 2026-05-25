package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import in.gov.cybercrime.sachet.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeDocumentService {

    private final NoticeService noticeService;
    private static final int MAX_STORED_FILE_NAME_LENGTH = 180;
    private static final int MAX_DISPLAY_FILE_NAME_LENGTH = 512;

    @Value("${sachet.storage.notice-documents-root:${user.home}/sachet/notice-documents}")
    private String noticeDocumentsRoot;

    public Resource getDispatchDocument(Long noticeId) {
        return loadResource(
                getDispatchDocumentInfo(noticeId),
                "Dispatch"
        );
    }

    public DocumentInfo getDispatchDocumentInfo(Long noticeId) {
        Notice notice = noticeService.getById(noticeId);

        return requireDocumentInfo(
                notice.getDispatch() != null
                        ? notice.getDispatch().getDocument()
                        : null,
                "Dispatch"
        );
    }

    public Resource getReplyDocument(Long noticeId) {
        return loadResource(
                getReplyDocumentInfo(noticeId),
                "Reply"
        );
    }

    public DocumentInfo getReplyDocumentInfo(Long noticeId) {
        Notice notice = noticeService.getById(noticeId);

        return requireDocumentInfo(
                notice.getReply() != null
                        ? notice.getReply().getDocument()
                        : null,
                "Reply"
        );
    }

    public Notice storeDispatchDocument(Long noticeId, String issuedTo, java.time.LocalDate issuedDate, MultipartFile file) {
        DocumentInfo documentInfo = storeDocument(noticeId, "dispatch", file);
        return noticeService.attachDispatchDocument(noticeId, issuedTo, issuedDate, documentInfo);
    }

    public Notice storeReplyDocument(Long noticeId, java.time.LocalDate replyDate, String remarks, MultipartFile file) {
        DocumentInfo documentInfo = storeDocument(noticeId, "reply", file);
        return noticeService.attachReplyDocument(noticeId, replyDate, remarks, documentInfo);
    }

    private DocumentInfo storeDocument(Long noticeId, String type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document file is required");
        }

        String originalFileName = String.valueOf(file.getOriginalFilename() != null ? file.getOriginalFilename() : "").trim();
        if (originalFileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document file name is required");
        }

        try {
            Path storageRoot = getStorageRoot();
            Path storageDirectory = storageRoot.resolve(type).resolve("notice-" + noticeId).normalize();
            Files.createDirectories(storageDirectory);

            String safeFileName = buildStoredFileName(originalFileName);
            Path targetPath = storageDirectory.resolve(safeFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setFileName(truncate(originalFileName, MAX_DISPLAY_FILE_NAME_LENGTH));
            documentInfo.setFilePath(toStoredPath(storageRoot, targetPath));
            return documentInfo;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store document file", exception);
        }
    }

    public Path getDocumentPath(DocumentInfo document) {
        String filePath = document.getFilePath();
        Path path = Paths.get(filePath);

        if (path.isAbsolute()) {
            return path.normalize();
        }

        Path storageRoot = getStorageRoot();
        Path resolvedPath = storageRoot.resolve(path).normalize();
        if (!resolvedPath.startsWith(storageRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid document file path");
        }

        return resolvedPath;
    }

    private Path getStorageRoot() {
        return Paths.get(noticeDocumentsRoot).toAbsolutePath().normalize();
    }

    private String toStoredPath(Path storageRoot, Path targetPath) {
        Path normalizedTarget = targetPath.toAbsolutePath().normalize();
        if (!normalizedTarget.startsWith(storageRoot)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document path is outside storage root");
        }

        return storageRoot.relativize(normalizedTarget).toString().replace('\\', '/');
    }

    private String sanitizeFileName(String fileName) {
        String sanitized = String.valueOf(fileName == null ? "" : fileName)
                .trim()
                .replaceAll("[\\p{Cntrl}]+", "_")
                .replaceAll("[\\\\/:*?\"<>|]+", "_")
                .replaceAll("\\s+", "_");
        return sanitized.isBlank() ? "document" : sanitized;
    }

    private String buildStoredFileName(String originalFileName) {
        String sanitized = truncate(sanitizeFileName(originalFileName), MAX_STORED_FILE_NAME_LENGTH);
        return UUID.randomUUID() + "_" + sanitized;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private DocumentInfo requireDocumentInfo(
            DocumentInfo document,
            String documentLabel
    ) {

        if (document == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    documentLabel + " document not found"
            );
        }

        String filePath = document.getFilePath();

        if (filePath == null || filePath.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    documentLabel + " document is not available for download"
            );
        }

        return document;
    }

    private Resource loadResource(
            DocumentInfo document,
            String documentLabel
    ) {

        Path filePath = getDocumentPath(document);

        Resource resource = new PathResource(filePath);

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    documentLabel + " document file not found"
            );
        }

        return resource;
    }
}

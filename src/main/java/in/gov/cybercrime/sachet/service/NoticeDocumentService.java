package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.DocumentInfo;
import in.gov.cybercrime.sachet.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class NoticeDocumentService {

    private final NoticeService noticeService;

    public Resource getDispatchDocument(Long noticeId) {

        Notice notice = noticeService.getById(noticeId);

        if (
                notice.getDispatch() == null ||
                notice.getDispatch().getDocument() == null
        ) {
            throw new RuntimeException("Dispatch document not found");
        }

        return loadResource(
                notice.getDispatch().getDocument()
        );
    }

    public Resource getReplyDocument(Long noticeId) {

        Notice notice = noticeService.getById(noticeId);

        if (
                notice.getReply() == null ||
                notice.getReply().getDocument() == null
        ) {
            throw new RuntimeException("Reply document not found");
        }

        return loadResource(
                notice.getReply().getDocument()
        );
    }

    private Resource loadResource(DocumentInfo document) {

        try {

            Path filePath = Paths.get(
                    document.getFilePath()
            ).normalize();

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

            return resource;

        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid file path");
        }
    }
}
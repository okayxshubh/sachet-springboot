package in.gov.cybercrime.sachet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocTemplateResponse {
    private Long id;
    private Long docTypeId;
    private String docTypeName;
    private String fileName;
    private String filePath;
    private String content;
    private String contentBase64;
    private String encoding;
    private String mimeType;
}

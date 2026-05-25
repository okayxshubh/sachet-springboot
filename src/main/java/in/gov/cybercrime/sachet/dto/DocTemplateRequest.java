package in.gov.cybercrime.sachet.dto;

import lombok.Data;

@Data
public class DocTemplateRequest {
    private Long id;
    private Long docTypeId;
    private String content;
    private String contentBase64;
}

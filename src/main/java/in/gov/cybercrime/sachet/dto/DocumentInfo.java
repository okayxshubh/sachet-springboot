package in.gov.cybercrime.sachet.dto;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class DocumentInfo {

    private String fileName;

    private String filePath;
}
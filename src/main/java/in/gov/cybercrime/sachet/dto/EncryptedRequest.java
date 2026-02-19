package in.gov.cybercrime.sachet.dto;


// For all types of encrypted requests
public class EncryptedRequest {
    private String payload; // The fully encrypted base64 string
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}

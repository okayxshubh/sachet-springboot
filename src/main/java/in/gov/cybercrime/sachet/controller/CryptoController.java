package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.encryption.EncryptedPayload;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private final ObjectMapper objectMapper;

    public CryptoController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/encrypt", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> encryptPlain(@RequestBody String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return ResponseEntity.badRequest().body("Empty payload");
        }
        String encrypted = SachetCrypto.encrypt(plainText);
        return ResponseEntity.ok(encrypted);
    }

    @PostMapping(value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> encryptJson(@RequestBody JsonNode body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            String encrypted = SachetCrypto.encrypt(json);
            return ResponseEntity.ok(encrypted);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Encryption failed");
        }
    }

    @PostMapping(value = "/decrypt", consumes = MediaType.TEXT_PLAIN_VALUE)
    public GenericResponse<Object> decryptPlain(@RequestBody String encrypted) {
        if (encrypted == null || encrypted.isBlank()) {
            return GenericResponse.fail("Empty payload");
        }
        String decrypted = SachetCrypto.decrypt(encrypted);
        return GenericResponse.ok("Decrypted", decrypted);
    }

    @PostMapping(value = "/decrypt", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse<Object> decryptJson(@RequestBody EncryptedPayload request) {
        try {
            String decrypted = SachetCrypto.decrypt(request.getPayload());
            try {
                JsonNode json = objectMapper.readTree(decrypted);
                return GenericResponse.ok("Decrypted", json);
            } catch (Exception ignored) {
                return GenericResponse.ok("Decrypted", decrypted);
            }
        } catch (Exception ex) {
            return GenericResponse.fail("Decryption failed");
        }
    }
}

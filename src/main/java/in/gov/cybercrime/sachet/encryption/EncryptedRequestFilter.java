package in.gov.cybercrime.sachet.encryption;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class EncryptedRequestFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public EncryptedRequestFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip filtering for GET, OPTIONS, /api/crypto/** and /api/auth/get-token
        if (!path.startsWith("/api/")) return true;
        if (path.startsWith("/api/crypto/")) return true;
        if ("/api/auth/get-token".equals(path)) return true; // skip filter for get token
        if ("/api/dashboard/system-summary".equals(path)) return true; // Skip encryption for system summary
        if ("GET".equalsIgnoreCase(method)) return true;
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            filterChain.doFilter(request, response);
            return;
        }

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (body == null || body.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(body);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid encrypted payload format.");
            return;
        }

        JsonNode payloadNode = root.get("payload");
        if (payloadNode == null || payloadNode.isNull() || payloadNode.asText().isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing encrypted payload.");
            return;
        }

        String decryptedJson;
        try {
            decryptedJson = SachetCrypto.decrypt(payloadNode.asText());
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid encrypted payload.");
            return;
        }

        byte[] decryptedBytes = decryptedJson.getBytes(StandardCharsets.UTF_8);
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request, decryptedBytes);
        filterChain.doFilter(wrapped, response);
    }
}

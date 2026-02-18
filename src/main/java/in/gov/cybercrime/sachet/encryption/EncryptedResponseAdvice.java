package in.gov.cybercrime.sachet.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class EncryptedResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public EncryptedResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (!(request instanceof ServletServerHttpRequest)) return body;
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String path = servletRequest.getRequestURI();
        if (path == null || !path.startsWith("/api/")) return body;
        if (path.startsWith("/api/crypto/")) return body;
        if (body instanceof EncryptedPayload) return body;

        try {
            String json = objectMapper.writeValueAsString(body);
            return new EncryptedPayload(SachetCrypto.encrypt(json));
        } catch (Exception ex) {
            return body;
        }
    }
}

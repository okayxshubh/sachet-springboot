package in.gov.cybercrime.sachet.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiRateLimitFilterTest {

    @Test
    void allowsRequestsWithinLimit() throws ServletException, IOException {
        ApiRateLimitFilter filter = new ApiRateLimitFilter(true, 2, Duration.ofMinutes(1));

        MockHttpServletRequest firstRequest = buildRequest("127.0.0.1");
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilter(firstRequest, firstResponse, new MockFilterChain());

        MockHttpServletRequest secondRequest = buildRequest("127.0.0.1");
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        filter.doFilter(secondRequest, secondResponse, new MockFilterChain());

        assertEquals(200, firstResponse.getStatus());
        assertEquals(200, secondResponse.getStatus());
    }

    @Test
    void blocksRequestsAfterLimitIsExceeded() throws ServletException, IOException {
        ApiRateLimitFilter filter = new ApiRateLimitFilter(true, 1, Duration.ofMinutes(1));

        MockHttpServletRequest firstRequest = buildRequest("127.0.0.1");
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilter(firstRequest, firstResponse, new MockFilterChain());

        MockHttpServletRequest secondRequest = buildRequest("127.0.0.1");
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        filter.doFilter(secondRequest, secondResponse, new MockFilterChain());

        assertEquals(200, firstResponse.getStatus());
        assertEquals(429, secondResponse.getStatus());
        assertTrue(secondResponse.getContentType().startsWith("application/json"));
    }

    @Test
    void skipsOptionsRequests() throws ServletException, IOException {
        ApiRateLimitFilter filter = new ApiRateLimitFilter(true, 1, Duration.ofMinutes(1));

        MockHttpServletRequest optionsRequest = new MockHttpServletRequest("OPTIONS", "/api/auth/login");
        optionsRequest.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse optionsResponse = new MockHttpServletResponse();
        filter.doFilter(optionsRequest, optionsResponse, new MockFilterChain());

        MockHttpServletRequest postRequest = buildRequest("127.0.0.1");
        MockHttpServletResponse postResponse = new MockHttpServletResponse();
        filter.doFilter(postRequest, postResponse, new MockFilterChain());

        assertEquals(200, optionsResponse.getStatus());
        assertEquals(200, postResponse.getStatus());
    }

    private MockHttpServletRequest buildRequest(String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr(remoteAddr);
        return request;
    }
}

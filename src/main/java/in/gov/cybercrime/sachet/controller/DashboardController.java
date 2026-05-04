package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.CaseDashboardResponse;
import in.gov.cybercrime.sachet.dto.DashboardRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.SystemDashboardResponse;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    private GenericResponse<String> success(Object data, String message) throws Exception {
        String responseJson = objectMapper.writeValueAsString(data);
        String encryptedData = SachetCrypto.encrypt(responseJson);

        return GenericResponse.<String>builder()
                .status("OK")
                .message(message)
                .data(encryptedData)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/case-summary")
    public GenericResponse<String> getCaseSummary(@RequestBody String encryptedBody) throws Exception {
        String json = SachetCrypto.decrypt(encryptedBody);
        DashboardRequest request = objectMapper.readValue(json, DashboardRequest.class);
        CaseDashboardResponse response = dashboardService.getCaseDashboard(request.getCaseId());
        return success(response, "Dashboard summary fetched successfully");
    }

    @PostMapping("/system-summary")
    public GenericResponse<String> getSystemSummary(@RequestBody(required = false) String body) throws Exception {
        // For debugging, accept any body
        SystemDashboardResponse response = dashboardService.getSystemDashboard();
        return success(response, "System dashboard summary fetched successfully");
    }
}

package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.entity.AuditLog;
import in.gov.cybercrime.sachet.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public GenericResponse<List<AuditLog>> list(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive) {
        return GenericResponse.ok(auditLogService.listLogs(includeInactive));
    }
}

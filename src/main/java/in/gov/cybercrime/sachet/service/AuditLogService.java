package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.entity.AuditLog;
import in.gov.cybercrime.sachet.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> listLogs(boolean includeInactive) {
        return includeInactive ? auditLogRepository.findAll() : auditLogRepository.findByIsActiveTrue();
    }
}

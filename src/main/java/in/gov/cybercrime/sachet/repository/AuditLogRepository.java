package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByIsActiveTrue();
}

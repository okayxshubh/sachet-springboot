package in.gov.cybercrime.sachet.repository;

import in.gov.cybercrime.sachet.entity.NcrpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NcrpTransactionRepository extends JpaRepository<NcrpTransaction, Long> {
    List<NcrpTransaction> findByCaseFileIdAndIsActiveTrue(Long caseId);
}

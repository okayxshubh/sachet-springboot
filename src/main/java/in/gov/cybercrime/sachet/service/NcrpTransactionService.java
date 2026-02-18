package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.NcrpTransactionRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.entity.NcrpTransaction;
import in.gov.cybercrime.sachet.repository.CaseFileRepository;
import in.gov.cybercrime.sachet.repository.NcrpTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NcrpTransactionService {

    private final NcrpTransactionRepository transactionRepository;
    private final CaseFileRepository caseFileRepository;

    public NcrpTransactionService(NcrpTransactionRepository transactionRepository,
                                  CaseFileRepository caseFileRepository) {
        this.transactionRepository = transactionRepository;
        this.caseFileRepository = caseFileRepository;
    }

    public List<NcrpTransaction> listByCase(Long caseId) {
        return transactionRepository.findByCaseFileIdAndIsActiveTrue(caseId);
    }

    public NcrpTransaction create(Long caseId, NcrpTransactionRequest request) {
        CaseFile caseFile = getCase(caseId);
        NcrpTransaction transaction = new NcrpTransaction();
        transaction.setCaseFile(caseFile);
        transaction.setAckNo(request.getAckNo());
        transaction.setBankName(request.getBankName());
        transaction.setAccountNo(request.getAccountNo());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setAmount(request.getAmount());
        transaction.setLayer(request.getLayer());
        return transactionRepository.save(transaction);
    }

    public NcrpTransaction update(Long id, NcrpTransactionRequest request) {
        NcrpTransaction transaction = getTransaction(id);
        if (request.getAckNo() != null) transaction.setAckNo(request.getAckNo());
        if (request.getBankName() != null) transaction.setBankName(request.getBankName());
        if (request.getAccountNo() != null) transaction.setAccountNo(request.getAccountNo());
        if (request.getTransactionDate() != null) transaction.setTransactionDate(request.getTransactionDate());
        if (request.getAmount() != null) transaction.setAmount(request.getAmount());
        if (request.getLayer() != null) transaction.setLayer(request.getLayer());
        return transactionRepository.save(transaction);
    }

    private NcrpTransaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    private CaseFile getCase(Long id) {
        return caseFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }
}

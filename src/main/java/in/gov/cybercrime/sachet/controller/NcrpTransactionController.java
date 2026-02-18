package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NcrpTransactionRequest;
import in.gov.cybercrime.sachet.entity.NcrpTransaction;
import in.gov.cybercrime.sachet.service.NcrpTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NcrpTransactionController {

    private final NcrpTransactionService transactionService;

    public NcrpTransactionController(NcrpTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/api/cases/{caseId}/transactions")
    public GenericResponse<List<NcrpTransaction>> list(@PathVariable Long caseId) {
        return GenericResponse.ok(transactionService.listByCase(caseId));
    }

    @PostMapping("/api/cases/{caseId}/transactions")
    public GenericResponse<NcrpTransaction> create(@PathVariable Long caseId,
                                                   @RequestBody NcrpTransactionRequest request) {
        return GenericResponse.ok(transactionService.create(caseId, request));
    }

    @PutMapping("/api/transactions/{id}")
    public GenericResponse<NcrpTransaction> update(@PathVariable Long id,
                                                   @RequestBody NcrpTransactionRequest request) {
        return GenericResponse.ok(transactionService.update(id, request));
    }
}


package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.CaseIdRequest;
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

    @PostMapping("/api/transactions/list")
    public GenericResponse<List<NcrpTransaction>> list(@RequestBody CaseIdRequest request) {
        return GenericResponse.ok(transactionService.listByCase(request.getCaseId()));
    }

    @PostMapping("/api/transactions/create")
    public GenericResponse<NcrpTransaction> create(@RequestBody NcrpTransactionRequest request) {
        return GenericResponse.ok(transactionService.create(request.getCaseId(), request));
    }

    @PutMapping("/api/transactions/update")
    public GenericResponse<NcrpTransaction> update(@RequestBody NcrpTransactionRequest request) {
        return GenericResponse.ok(transactionService.update(request.getId(), request));
    }
}


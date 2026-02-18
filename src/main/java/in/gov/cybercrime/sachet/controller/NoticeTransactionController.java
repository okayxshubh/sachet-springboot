package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeTransactionRequest;
import in.gov.cybercrime.sachet.entity.NoticeTransaction;
import in.gov.cybercrime.sachet.service.NoticeTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeTransactionController {

    private final NoticeTransactionService noticeTransactionService;

    public NoticeTransactionController(NoticeTransactionService noticeTransactionService) {
        this.noticeTransactionService = noticeTransactionService;
    }

    @GetMapping("/api/notices/{noticeId}/transactions")
    public GenericResponse<List<NoticeTransaction>> list(@PathVariable Long noticeId) {
        return GenericResponse.ok(noticeTransactionService.listByNotice(noticeId));
    }

    @PostMapping("/api/notices/{noticeId}/transactions")
    public GenericResponse<NoticeTransaction> create(@PathVariable Long noticeId,
                                                     @RequestBody NoticeTransactionRequest request) {
        return GenericResponse.ok(noticeTransactionService.create(noticeId, request));
    }

    @DeleteMapping("/api/notices/{noticeId}/transactions/{transactionId}")
    public GenericResponse<String> delete(@PathVariable Long noticeId, @PathVariable Long transactionId) {
        noticeTransactionService.delete(noticeId, transactionId);
        return GenericResponse.ok("Mapping removed", "OK");
    }
}


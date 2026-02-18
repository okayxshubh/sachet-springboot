package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/api/cases/{caseId}/notices")
    public GenericResponse<List<Notice>> list(@PathVariable Long caseId) {
        return GenericResponse.ok(noticeService.listByCase(caseId));
    }

    @PostMapping("/api/cases/{caseId}/notices")
    public GenericResponse<Notice> create(@PathVariable Long caseId, @RequestBody NoticeRequest request) {
        return GenericResponse.ok(noticeService.create(caseId, request));
    }

    @PutMapping("/api/notices/{id}")
    public GenericResponse<Notice> update(@PathVariable Long id, @RequestBody NoticeRequest request) {
        return GenericResponse.ok(noticeService.update(id, request));
    }
}


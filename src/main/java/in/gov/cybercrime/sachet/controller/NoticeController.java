package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.CaseIdRequest;
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

    @PostMapping("/api/notices/list")
    public GenericResponse<List<Notice>> list(@RequestBody CaseIdRequest request) {
        return GenericResponse.ok(noticeService.listByCase(request.getCaseId()));
    }

    @PostMapping("/api/notices/create")
    public GenericResponse<Notice> create(@RequestBody NoticeRequest request) {
        return GenericResponse.ok(noticeService.create(request.getCaseId(), request));
    }

    @PutMapping("/api/notices/update")
    public GenericResponse<Notice> update(@RequestBody NoticeRequest request) {
        return GenericResponse.ok(noticeService.update(request.getId(), request));
    }
}


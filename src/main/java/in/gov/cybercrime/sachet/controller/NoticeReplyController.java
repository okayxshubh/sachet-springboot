package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeReplyRequest;
import in.gov.cybercrime.sachet.entity.NoticeReply;
import in.gov.cybercrime.sachet.service.NoticeReplyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeReplyController {

    private final NoticeReplyService noticeReplyService;

    public NoticeReplyController(NoticeReplyService noticeReplyService) {
        this.noticeReplyService = noticeReplyService;
    }

    @GetMapping("/api/notices/{noticeId}/replies")
    public GenericResponse<List<NoticeReply>> list(@PathVariable Long noticeId) {
        return GenericResponse.ok(noticeReplyService.listByNotice(noticeId));
    }

    @PostMapping("/api/notices/{noticeId}/replies")
    public GenericResponse<NoticeReply> create(@PathVariable Long noticeId, @RequestBody NoticeReplyRequest request) {
        return GenericResponse.ok(noticeReplyService.create(noticeId, request));
    }

    @PutMapping("/api/replies/{id}")
    public GenericResponse<NoticeReply> update(@PathVariable Long id, @RequestBody NoticeReplyRequest request) {
        return GenericResponse.ok(noticeReplyService.update(id, request));
    }
}


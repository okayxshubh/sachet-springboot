package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeIdRequest;
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

    @PostMapping("/api/replies/list")
    public GenericResponse<List<NoticeReply>> list(@RequestBody NoticeIdRequest request) {
        return GenericResponse.ok(noticeReplyService.listByNotice(request.getNoticeId()));
    }

    @PostMapping("/api/replies/create")
    public GenericResponse<NoticeReply> create(@RequestBody NoticeReplyRequest request) {
        return GenericResponse.ok(noticeReplyService.create(request.getNoticeId(), request));
    }

    @PutMapping("/api/replies/update")
    public GenericResponse<NoticeReply> update(@RequestBody NoticeReplyRequest request) {
        return GenericResponse.ok(noticeReplyService.update(request.getId(), request));
    }
}


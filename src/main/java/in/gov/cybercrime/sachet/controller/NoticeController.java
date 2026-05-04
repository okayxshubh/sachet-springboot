package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.NoticeRequest;
import in.gov.cybercrime.sachet.entity.Notice;
import in.gov.cybercrime.sachet.service.NoticeDocumentService;
import in.gov.cybercrime.sachet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final NoticeDocumentService noticeDocumentService;

    @PostMapping("/list")
    public GenericResponse<?> list(@RequestBody NoticeRequest request) {
        try {

            if (request.getId() != null) {
                return GenericResponse.ok(
                        noticeService.getById(request.getId())
                );
            }

            if (request.getCaseId() != null) {
                return GenericResponse.ok(
                        noticeService.listByCaseWithOptionalLayer(
                                request.getCaseId(),
                                request.getLayer()
                        )
                );
            }

            return GenericResponse.fail("caseId is required");

        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/create")
    public GenericResponse<Notice> create(@RequestBody NoticeRequest request) {
        try {
            return GenericResponse.ok(
                    noticeService.create(request.getCaseId(), request)
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public GenericResponse<Notice> update(@RequestBody NoticeRequest request) {
        try {
            return GenericResponse.ok(
                    noticeService.update(request.getId(), request)
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/detail")
    public GenericResponse<Notice> detail(@RequestBody NoticeRequest request) {
        try {
            return GenericResponse.ok(
                    noticeService.getById(request.getId())
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/sent")
    public GenericResponse<Notice> sent(@RequestBody NoticeRequest request) {
        try {
            return GenericResponse.ok(
                    noticeService.markSent(request.getId())
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping("/replied")
    public GenericResponse<Notice> replied(@RequestBody NoticeRequest request) {
        try {
            return GenericResponse.ok(
                    noticeService.markReplied(request.getId())
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public GenericResponse<String> delete(@RequestBody NoticeRequest request) {
        try {
            noticeService.delete(request.getId());
            return GenericResponse.ok("Deleted Successfully");
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }


//    Attach Docs
@PostMapping(value = "/attach-dispatch", consumes = MediaType.APPLICATION_JSON_VALUE)
public GenericResponse<Notice> attachDispatch(
        @RequestBody NoticeRequest request
) {
    try {

        return GenericResponse.ok(
                noticeService.attachDispatchDocument(
                        request.getId(),
                        request.getIssuedTo(),
                        request.getIssuedDate(),
                        request.getDocument()
                )
        );

    } catch (Exception ex) {
        return GenericResponse.fail(ex.getMessage());
    }
}

@PostMapping(value = {"/attach-dispatch", "/upload-dispatch"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public GenericResponse<Notice> attachDispatchWithFile(
        @RequestParam Long id,
        @RequestParam(required = false) String issuedTo,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate issuedDate,
        @RequestPart("document") MultipartFile document
) {
    try {
        return GenericResponse.ok(
                noticeDocumentService.storeDispatchDocument(id, issuedTo, issuedDate, document)
        );
    } catch (Exception ex) {
        return GenericResponse.fail(ex.getMessage());
    }
}

    @PostMapping(value = "/attach-reply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse<Notice> attachReply(
            @RequestBody NoticeRequest request
    ) {
        try {

            return GenericResponse.ok(
                    noticeService.attachReplyDocument(
                            request.getId(),
                            request.getReplyDate(),
                            request.getRemarks(),
                            request.getDocument()
                    )
            );

        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }

    @PostMapping(value = {"/attach-reply", "/upload-reply"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericResponse<Notice> attachReplyWithFile(
            @RequestParam Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate replyDate,
            @RequestParam(required = false) String remarks,
            @RequestPart("document") MultipartFile document
    ) {
        try {
            return GenericResponse.ok(
                    noticeDocumentService.storeReplyDocument(id, replyDate, remarks, document)
            );
        } catch (Exception ex) {
            return GenericResponse.fail(ex.getMessage());
        }
    }
}

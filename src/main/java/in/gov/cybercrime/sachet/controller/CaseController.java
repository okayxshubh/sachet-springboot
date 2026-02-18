package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.AssignCaseRequest;
import in.gov.cybercrime.sachet.dto.CaseCreateRequest;
import in.gov.cybercrime.sachet.dto.CaseUpdateRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.IdRequest;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.service.CaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public GenericResponse<List<CaseFile>> listCases(
            @RequestParam(name = "firNo", required = false) String firNo,
            @RequestParam(name = "firYear", required = false) Integer firYear,
            @RequestParam(name = "assignedToId", required = false) Long assignedToId) {
        return GenericResponse.ok(caseService.getCases(
                Optional.ofNullable(firNo),
                Optional.ofNullable(firYear),
                Optional.ofNullable(assignedToId)
        ));
    }

    @PostMapping
    public GenericResponse<CaseFile> createCase(@RequestBody CaseCreateRequest request) {
        return GenericResponse.ok(caseService.createCase(request));
    }

    @PostMapping("/get")
    public GenericResponse<CaseFile> getCase(@RequestBody IdRequest request) {
        return GenericResponse.ok(caseService.getCase(request.getId()));
    }

    @PutMapping("/update")
    public GenericResponse<CaseFile> updateCase(@RequestBody CaseUpdateRequest request) {
        return GenericResponse.ok(caseService.updateCase(request.getId(), request));
    }

    @PatchMapping("/assign")
    public GenericResponse<CaseFile> assignCase(@RequestBody AssignCaseRequest request) {
        return GenericResponse.ok(caseService.assignCase(request.getCaseId(), request));
    }
}


package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.AssignCaseRequest;
import in.gov.cybercrime.sachet.dto.CaseCreateRequest;
import in.gov.cybercrime.sachet.dto.CaseUpdateRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.entity.CaseFile;
import in.gov.cybercrime.sachet.service.CaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cases")
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

    @GetMapping("/{id}")
    public GenericResponse<CaseFile> getCase(@PathVariable Long id) {
        return GenericResponse.ok(caseService.getCase(id));
    }

    @PutMapping("/{id}")
    public GenericResponse<CaseFile> updateCase(@PathVariable Long id, @RequestBody CaseUpdateRequest request) {
        return GenericResponse.ok(caseService.updateCase(id, request));
    }

    @PatchMapping("/{id}/assign")
    public GenericResponse<CaseFile> assignCase(@PathVariable Long id, @RequestBody AssignCaseRequest request) {
        return GenericResponse.ok(caseService.assignCase(id, request));
    }
}

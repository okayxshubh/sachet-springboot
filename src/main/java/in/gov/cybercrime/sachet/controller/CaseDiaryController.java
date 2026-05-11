package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.CaseDiaryRequest;
import in.gov.cybercrime.sachet.dto.CaseDiaryResponse;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.service.CaseDiaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CaseDiaryController {

    private final CaseDiaryService caseDiaryService;

    public CaseDiaryController(CaseDiaryService caseDiaryService) {
        this.caseDiaryService = caseDiaryService;
    }

    @GetMapping("/api/cases/{caseId}/diaries")
    public GenericResponse<List<CaseDiaryResponse>> list(@PathVariable Long caseId) {
        return GenericResponse.ok(caseDiaryService.listByCase(caseId));
    }

    @PostMapping("/api/cases/{caseId}/diaries")
    public GenericResponse<CaseDiaryResponse> create(@PathVariable Long caseId, @RequestBody CaseDiaryRequest request) {
        return GenericResponse.ok(caseDiaryService.create(caseId, request));
    }

    @PutMapping("/api/diaries/{id}")
    public GenericResponse<CaseDiaryResponse> update(@PathVariable Long id, @RequestBody CaseDiaryRequest request) {
        return GenericResponse.ok(caseDiaryService.update(id, request));
    }
}

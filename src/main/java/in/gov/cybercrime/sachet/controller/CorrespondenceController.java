package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.CorrespondenceRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.entity.Correspondence;
import in.gov.cybercrime.sachet.service.CorrespondenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CorrespondenceController {

    private final CorrespondenceService correspondenceService;

    public CorrespondenceController(CorrespondenceService correspondenceService) {
        this.correspondenceService = correspondenceService;
    }

    @GetMapping("/api/cases/{caseId}/correspondence")
    public GenericResponse<List<Correspondence>> list(@PathVariable Long caseId) {
        return GenericResponse.ok(correspondenceService.listByCase(caseId));
    }

    @PostMapping("/api/cases/{caseId}/correspondence")
    public GenericResponse<Correspondence> create(@PathVariable Long caseId,
                                                  @RequestBody CorrespondenceRequest request) {
        return GenericResponse.ok(correspondenceService.create(caseId, request));
    }

    @PutMapping("/api/correspondence/{id}")
    public GenericResponse<Correspondence> update(@PathVariable Long id,
                                                  @RequestBody CorrespondenceRequest request) {
        return GenericResponse.ok(correspondenceService.update(id, request));
    }
}


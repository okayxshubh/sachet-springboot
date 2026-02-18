package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.AccusedRequest;
import in.gov.cybercrime.sachet.dto.ArrestStatusRequest;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.entity.Accused;
import in.gov.cybercrime.sachet.service.AccusedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccusedController {

    private final AccusedService accusedService;

    public AccusedController(AccusedService accusedService) {
        this.accusedService = accusedService;
    }

    @GetMapping("/api/cases/{caseId}/accused")
    public GenericResponse<List<Accused>> list(@PathVariable Long caseId) {
        return GenericResponse.ok(accusedService.listByCase(caseId));
    }

    @PostMapping("/api/cases/{caseId}/accused")
    public GenericResponse<Accused> create(@PathVariable Long caseId, @RequestBody AccusedRequest request) {
        return GenericResponse.ok(accusedService.create(caseId, request));
    }

    @PutMapping("/api/accused/{id}")
    public GenericResponse<Accused> update(@PathVariable Long id, @RequestBody AccusedRequest request) {
        return GenericResponse.ok(accusedService.update(id, request));
    }

    @PatchMapping("/api/accused/{id}/arrested")
    public GenericResponse<Accused> updateArrested(@PathVariable Long id, @RequestBody ArrestStatusRequest request) {
        return GenericResponse.ok(accusedService.updateArrested(id, request));
    }
}


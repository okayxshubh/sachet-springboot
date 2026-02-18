package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.AccusedRequest;
import in.gov.cybercrime.sachet.dto.ArrestStatusRequest;
import in.gov.cybercrime.sachet.dto.CaseIdRequest;
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

    @PostMapping("/api/accused/list")
    public GenericResponse<List<Accused>> list(@RequestBody CaseIdRequest request) {
        return GenericResponse.ok(accusedService.listByCase(request.getCaseId()));
    }

    @PostMapping("/api/accused/create")
    public GenericResponse<Accused> create(@RequestBody AccusedRequest request) {
        return GenericResponse.ok(accusedService.create(request.getCaseId(), request));
    }

    @PutMapping("/api/accused/update")
    public GenericResponse<Accused> update(@RequestBody AccusedRequest request) {
        return GenericResponse.ok(accusedService.update(request.getId(), request));
    }

    @PatchMapping("/api/accused/arrested")
    public GenericResponse<Accused> updateArrested(@RequestBody ArrestStatusRequest request) {
        return GenericResponse.ok(accusedService.updateArrested(request.getId(), request));
    }
}


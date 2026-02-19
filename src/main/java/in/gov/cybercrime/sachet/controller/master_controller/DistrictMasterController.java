package in.gov.cybercrime.sachet.controller.master_controller;

import in.gov.cybercrime.sachet.masters.DistrictMaster;
import in.gov.cybercrime.sachet.repository.master_repos.DistrictMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters/districts")
public class DistrictMasterController {

    private final DistrictMasterRepository repo;

    public DistrictMasterController(DistrictMasterRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<DistrictMaster> getAllDistricts() {
        return repo.findAll(Sort.by("id").ascending());
    }
}

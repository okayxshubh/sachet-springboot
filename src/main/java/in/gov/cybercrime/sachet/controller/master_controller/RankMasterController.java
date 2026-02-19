package in.gov.cybercrime.sachet.controller.master_controller;

import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.repository.master_repos.RankMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters/ranks")
public class RankMasterController {

    private final RankMasterRepository repo;

    public RankMasterController(RankMasterRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<RankMaster> getAllRanks() {
        return repo.findAll(Sort.by("id").ascending());
    }
}

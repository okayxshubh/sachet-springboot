package in.gov.cybercrime.sachet.controller.master_controller;

import in.gov.cybercrime.sachet.masters.RoleMaster;
import in.gov.cybercrime.sachet.repository.master_repos.RoleMasterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters/roles")
public class RoleMasterController {

    private final RoleMasterRepository repo;

    public RoleMasterController(RoleMasterRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<RoleMaster> getAllRoles() {
        return repo.findAll(Sort.by("id").ascending());
    }
}

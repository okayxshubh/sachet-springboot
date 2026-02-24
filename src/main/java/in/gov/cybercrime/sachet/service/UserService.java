package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.UserCreateRequest;
import in.gov.cybercrime.sachet.dto.UserResponse;
import in.gov.cybercrime.sachet.dto.UserUpdateRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.repository.master_repos.PoliceStationMasterRepository;
import in.gov.cybercrime.sachet.repository.master_repos.RankMasterRepository;
import in.gov.cybercrime.sachet.repository.master_repos.RoleMasterRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RankMasterRepository rankRepository;
    private final PoliceStationMasterRepository psRepository;
    private final RoleMasterRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RankMasterRepository rankRepository,
                       PoliceStationMasterRepository psRepository,
                       RoleMasterRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
        this.psRepository = psRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByFilters(Long rankId, Boolean isActive) {
        return userRepository.findByIsApprovedTrue().stream()
                .filter(user -> rankId == null
                        || (user.getRank() != null && rankId.equals(user.getRank().getId())))
                .filter(user -> isActive == null || isActive.equals(user.getIsActive()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getApprovalPoolUsers() {
        return userRepository.findByIsActiveTrueAndIsApprovedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(getUserEntity(id));
    }

    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }

        RankMaster rank = rankRepository.findById(request.getRankId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Rank not found"));

        PoliceStationMaster ps = psRepository.findById(request.getPsId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Police station not found"));

        RoleMaster role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));

        User user = new User();
        user.setName(request.getName());
        user.setRank(rank);
        user.setPs(ps);
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setIsApproved(false);

        return toResponse(userRepository.save(user));
    }

    public UserResponse approveUser(Long id) {
        User user = getUserEntity(id);
        user.setIsApproved(true);
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = getUserEntity(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getRankId() != null) {
            RankMaster rank = rankRepository.findById(request.getRankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Rank"));
            user.setRank(rank);
        }

        if (request.getPsId() != null) {
            PoliceStationMaster ps = psRepository.findById(request.getPsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Police Station"));
            user.setPs(ps);
        }

        if (request.getRoleId() != null) {
            RoleMaster role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Role"));
            user.setRole(role);
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Phone already exists");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getUpdatedBy() != null) {
            user.setUpdatedBy(request.getUpdatedBy());
        }

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id, String updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(false);
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getRank() != null ? user.getRank().getRankName() : null,
                user.getPs() != null ? user.getPs().getPsName() : null,
                user.getPs() != null && user.getPs().getDistrict() != null
                        ? user.getPs().getDistrict().getDistrictName()
                        : null,
                user.getPhone(),
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getIsActive(),
                user.getIsApproved()
        );
    }
}

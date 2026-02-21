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

    // Find Users by rank and is active
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsersByRank(Long rankId) {

        List<User> users =
                userRepository.findByRank_IdAndIsActiveTrue(rankId);

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    // Purpose: Fetch single user (works for active and inactive)
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(getUserEntity(id));
    }

    // Purpose: Create new user
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

        return toResponse(userRepository.save(user));
    }

    // Purpose: Update user
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

    // Purpose: Soft delete user
    public void deleteUser(Long id, String updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    // Purpose: Hard delete user
    public void hardDeleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Purpose: Fetch user entity
    @Transactional(readOnly = true)
    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Convert entity to response DTO
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
                user.getIsActive()
        );
    }
}
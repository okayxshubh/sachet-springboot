package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.*;
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
    private final PoliceStationMasterRepository policeStationRepository;
    private final RoleMasterRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RankMasterRepository rankRepository,
                       PoliceStationMasterRepository policeStationRepository,
                       RoleMasterRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
        this.policeStationRepository = policeStationRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // Register but Approval Pending
    public String register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        RankMaster rank = rankRepository.findById(request.getRankId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid rankId"));

        PoliceStationMaster ps = policeStationRepository.findById(request.getPsId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid psId"));

        RoleMaster role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid roleId"));

        User user = new User();
        user.setName(request.getName());
        user.setRank(rank);
        user.setPs(ps);
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setIsApproved(false);

        userRepository.save(user);
        return "User registered successfully";
    }

    // Approve User: Nodal officer can set any details while approving
    public UserResponse approveUser(ApproveUserRequest request) {

        User user = getUserEntity(request.getId());

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getRankId() != null) {
            RankMaster rank = rankRepository.findById(request.getRankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Rank"));
            user.setRank(rank);
        }

        if (request.getPsId() != null) {
            PoliceStationMaster ps = policeStationRepository.findById(request.getPsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Police Station"));
            user.setPs(ps);
        }

        if (request.getRoleId() != null) {
            RoleMaster role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Role"));
            user.setRole(role);
        }

        if (request.getPhone() != null &&
                !request.getPhone().isBlank() &&
                !request.getPhone().equals(user.getPhone())) {

            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Phone already exists");
            }
            user.setPhone(request.getPhone().trim());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getUpdatedBy() != null) {
            user.setUpdatedBy(request.getUpdatedBy());
        }

        // enforce required fields before approval
        if (user.getRole() == null ||
                user.getRank() == null ||
                user.getPs() == null) {

            throw new IllegalArgumentException("Complete user details before approval");
        }

        user.setIsApproved(true);

        return toResponse(userRepository.save(user));
    }



    /*
    * AFTER User is Approved and shown in list of users
    * */

    // Soft Deletion of User By Higher Authorities
    public void deleteUser(Long id, String updatedBy) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(false);
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    // Update User Request Only to Super Admin or Higher Officers
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = getUserEntity(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getRankId() != null) {
            RankMaster rank = rankRepository.findById(request.getRankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Rank"));
            user.setRank(rank);
        }

        if (request.getPsId() != null) {
            PoliceStationMaster ps = policeStationRepository.findById(request.getPsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Police Station"));
            user.setPs(ps);
        }

        if (request.getRoleId() != null) {
            RoleMaster role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Role"));
            user.setRole(role);
        }

        if (request.getPhone() != null
                && !request.getPhone().isBlank()
                && !request.getPhone().equals(user.getPhone())) {

            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Phone already exists");
            }
            user.setPhone(request.getPhone().trim());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getUpdatedBy() != null && !request.getUpdatedBy().isBlank()) {
            user.setUpdatedBy(request.getUpdatedBy().trim());
        }

        return toResponse(userRepository.save(user));
    }



    /*
    * HELPER METHODS BELOW
    * */

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
        return userRepository.findByIsApprovedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(getUserEntity(id));
    }

}

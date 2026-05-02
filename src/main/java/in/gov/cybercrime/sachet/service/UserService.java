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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    public String register(RegisterRequest request) {

        if (userRepository.existsByPhoneAndIsApprovedFalse(request.getPhone())) {
            throw new IllegalStateException(
                    "Registration already pending. Contact authorities.");
        }

        if (userRepository.existsByPhoneAndIsApprovedTrue(request.getPhone())) {
            throw new IllegalStateException(
                    "User already registered and approved.");
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

    // reject
    public void rejectUser(Long id, String rejectedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsApproved())) {
            throw new IllegalStateException("Approved users cannot be rejected");
        }

        userRepository.delete(user); // hard delete
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

    public List<UserResponse> approveUsers(List<ApproveUserRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("User approval list cannot be empty");
        }

        return requests.stream()
                .map(this::approveUser)
                .toList();
    }



    /*
    * AFTER User is Approved and shown in list of users
    * */

    // Soft Deletion of User + No Self Deletion
    public void deleteUser(Long id, String updatedBy) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized action");
        }

        String currentUser = auth.getName();

        // Prevent self deletion
        if (user.getPhone() != null && user.getPhone().equals(currentUser)) {
            throw new IllegalStateException("Cannot delete currently logged-in user");
        }

        // Prevent redundant delete
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new IllegalStateException("User already inactive");
        }

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
    public List<UserResponse> getUsersByDistrictAndPs(
            Long rankId,
            Long districtId,
            Long psId) {

        if (rankId == null) {
            throw new IllegalArgumentException("Rank Id required");
        }

        if (districtId == null) {
            throw new IllegalArgumentException("District Id is required");
        }

        if (psId == null) {
            throw new IllegalArgumentException("Police Station ID is required");
        }

        return userRepository
                .findByRankIdAndIsApprovedTrueAndIsActiveTrueAndPs_District_IdAndPs_Id(
                        rankId,
                        districtId,
                        psId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserMiniResponse> getUsersByRanks(
            Long rankId,
            Long districtId,
            Long psId) {

        if (rankId == null) {
            throw new IllegalArgumentException("Rank Id required");
        }

        if (districtId == null) {
            throw new IllegalArgumentException("District Id is required");
        }

        if (psId == null) {
            throw new IllegalArgumentException("Police Station ID is required");
        }

        return userRepository
                .findByRankIdAndIsApprovedTrueAndIsActiveTrueAndPs_District_IdAndPs_Id(
                        rankId,
                        districtId,
                        psId
                )
                .stream()
                .map(user -> new UserMiniResponse(
                        user.getId(),
                        user.getName(),
                        user.getPhone()
                ))
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

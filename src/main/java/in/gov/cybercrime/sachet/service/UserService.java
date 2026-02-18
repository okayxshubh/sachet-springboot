package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.UserCreateRequest;
import in.gov.cybercrime.sachet.dto.UserResponse;
import in.gov.cybercrime.sachet.dto.UserUpdateRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.entity.UserRole;
import in.gov.cybercrime.sachet.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getUsers(boolean includeInactive) {
        List<User> users = includeInactive ? userRepository.findAll() : userRepository.findByIsActiveTrue();
        return users.stream().map(this::toResponse).toList();
    }

    public UserResponse getUser(Long id) {
        return toResponse(getUserEntity(id));
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setRank(request.getRank());
        user.setPsName(request.getPsName());
        user.setDistrict(request.getDistrict());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserEntity(id);

        if (request.getName() != null) user.setName(request.getName());
        if (request.getRank() != null) user.setRank(request.getRank());
        if (request.getPsName() != null) user.setPsName(request.getPsName());
        if (request.getDistrict() != null) user.setDistrict(request.getDistrict());
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Phone already exists");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(Long id, boolean isActive) {
        User user = getUserEntity(id);
        user.setIsActive(isActive);
        return toResponse(userRepository.save(user));
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getRank(),
                user.getPsName(),
                user.getDistrict(),
                user.getPhone(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

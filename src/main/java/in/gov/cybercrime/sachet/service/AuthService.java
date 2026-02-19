package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import in.gov.cybercrime.sachet.repository.RoleRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.repository.master_repos.PoliceStationMasterRepository;
import in.gov.cybercrime.sachet.repository.master_repos.RankMasterRepository;
import in.gov.cybercrime.sachet.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RankMasterRepository rankRepository;
    private final PoliceStationMasterRepository policeStationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       RankMasterRepository rankRepository,
                       PoliceStationMasterRepository policeStationRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {

        // Purpose: Inject dependencies
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
        this.policeStationRepository = policeStationRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Purpose: Register new user
    public String register(RegisterRequest request) {

        // Purpose: Prevent duplicate phone registration
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        // Purpose: Fetch rank master using rankId
        RankMaster rank = rankRepository.findById(request.getRankId())
                .orElseThrow(() -> new RuntimeException("Invalid rankId"));

        // Purpose: Fetch police station master using psId
        PoliceStationMaster ps = policeStationRepository.findById(request.getPsId())
                .orElseThrow(() -> new RuntimeException("Invalid psId"));

        // Purpose: Fetch role master using roleId
        RoleMaster role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Invalid roleId"));

        // Purpose: Create user object and map all master entities
        User user = new User();
        user.setName(request.getName());
        user.setRank(rank);
        user.setPs(ps);
        user.setPhone(request.getPhone());
        user.setRole(role);

        // Purpose: Hash password before saving
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Purpose: Enable user by default
        user.setEnabled(true);

        // Purpose: Save user in DB
        userRepository.save(user);

        return "User registered successfully";
    }

    // Purpose: Login and return JWT + profile details
    public AuthResponse login(LoginRequest request) {

        // Purpose: Find user by phone number
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalid phone or password"));

        // Purpose: Block inactive user
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("User inactive");
        }

        // Purpose: Block disabled user
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new RuntimeException("User disabled");
        }

        // Purpose: Validate password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid phone or password");
        }

        // Purpose: Ensure role exists for JWT and security
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new RuntimeException("User role missing");
        }

        // Purpose: Convert role name into JWT standard format
        String roleName = user.getRole().getRoleName().trim().toUpperCase();

        // Purpose: Generate access token
        String token = jwtUtil.generateToken(user.getPhone(), roleName);

        // Purpose: Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(user.getPhone(), roleName);

        // Purpose: Return full response as per AuthResponse DTO
        return new AuthResponse(
                user.getName(),
                user.getRole(),
                user.getRank(),
                user.getPs(),
                token,
                refreshToken
        );
    }

    // Purpose: Refresh JWT using refresh token
    public AuthResponse refreshToken(String refreshToken) {

        // Purpose: Validate refresh token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Purpose: Extract phone from refresh token
        String phone = jwtUtil.extractUsername(refreshToken);

        // Purpose: Fetch user again from DB
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Purpose: Block inactive user
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("User inactive");
        }

        // Purpose: Block disabled user
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new RuntimeException("User disabled");
        }

        // Purpose: Ensure role exists
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new RuntimeException("User role missing");
        }

        // Purpose: Convert role name into JWT standard format
        String roleName = user.getRole().getRoleName().trim().toUpperCase();

        // Purpose: Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getPhone(), roleName);

        // Purpose: Generate new refresh token
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getPhone(), roleName);

        // Purpose: Return refreshed response as per AuthResponse DTO
        return new AuthResponse(
                user.getName(),
                user.getRole(),
                user.getRank(),
                user.getPs(),
                newAccessToken,
                newRefreshToken
        );
    }
}

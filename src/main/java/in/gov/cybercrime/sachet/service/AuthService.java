package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.exceptions.InvalidCredentialsException;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
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

        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
        this.policeStationRepository = policeStationRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

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

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid phone or password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException("User inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid phone or password");
        }

        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new IllegalArgumentException("User role missing");
        }

        String roleName = user.getRole().getRoleName().trim().toUpperCase();

        String token = jwtUtil.generateToken(user.getPhone(), roleName);
        String refreshToken = jwtUtil.generateRefreshToken(user.getPhone(), roleName);

        jwtUtil.validateRefreshToken(refreshToken);

        return new AuthResponse(
                user.getName(),
                user.getRole(),
                user.getRank(),
                user.getPs(),
                token,
                refreshToken
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String phone = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException("User inactive");
        }

        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new IllegalArgumentException("User role missing");
        }

        String roleName = user.getRole().getRoleName().trim().toUpperCase();

        String newAccessToken = jwtUtil.generateToken(user.getPhone(), roleName);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getPhone(), roleName);

        return new AuthResponse(
                user.getName(),
                user.getRole(),
                user.getRank(),
                user.getPs(),
                newAccessToken,
                newRefreshToken
        );
    }

    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token);
    }
}
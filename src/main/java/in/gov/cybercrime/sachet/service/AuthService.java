package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.entity.UserRole;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {

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

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalid phone or password"));
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("User is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid phone or password");
        }

        String token = jwtUtil.generateToken(user.getPhone(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getPhone(), user.getRole().name());

        return new AuthResponse(token, user.getName(), user.getRole().name(), refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String phone = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("User is inactive");
        }

        String newAccessToken = jwtUtil.generateToken(user.getPhone(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getPhone(), user.getRole().name());
        return new AuthResponse(newAccessToken, user.getName(), user.getRole().name(), newRefreshToken);
    }
}

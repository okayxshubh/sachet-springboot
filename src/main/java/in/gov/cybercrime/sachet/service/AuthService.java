package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.masters.RoleMaster;
import in.gov.cybercrime.sachet.repository.RoleRepository;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        RoleMaster role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getRoleName());

        return new AuthResponse(token, user.getUsername(), user.getRole().getRoleName());
    }
}

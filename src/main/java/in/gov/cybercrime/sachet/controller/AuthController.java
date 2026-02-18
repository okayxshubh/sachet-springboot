package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.GenericResponse;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RefreshTokenRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.dto.UserResponse;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public GenericResponse<String> register(@RequestBody RegisterRequest request) {
        return GenericResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public GenericResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return GenericResponse.ok("Login success", authService.login(request));
    }

    @PostMapping("/refresh")
    public GenericResponse<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return GenericResponse.ok("Token refreshed", authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public GenericResponse<String> logout() {
        return GenericResponse.ok("Logout success", "OK");
    }

    @GetMapping("/me")
    public GenericResponse<UserResponse> me(Authentication authentication) {
        String phone = authentication.getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse(
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

        return GenericResponse.ok(response);
    }
}


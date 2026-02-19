package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
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
    private final ObjectMapper objectMapper;

    public AuthController(AuthService authService, UserRepository userRepository) {
        // Inject dependencies
        this.authService = authService;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/register")
    public GenericResponse<String> register(@RequestBody RegisterRequest request) {
        // Register a new user
        return GenericResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public GenericResponse<AuthResponse> login(@RequestBody EncryptedRequest request) {
        try {
            // Decrypt payload from client
            String decryptedJson = SachetCrypto.decrypt(request.getPayload());

            // Convert decrypted JSON into LoginRequest
            LoginRequest loginRequest = objectMapper.readValue(decryptedJson, LoginRequest.class);

            // Authenticate user and generate JWT
            AuthResponse authResponse = authService.login(loginRequest);

            return GenericResponse.ok("Login success", authResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.fail("Server error");
        }
    }

    @PostMapping("/refresh")
    public GenericResponse<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // Refresh access token using refresh token
        return GenericResponse.ok("Token refreshed", authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public GenericResponse<String> logout() {
        // Logout (frontend deletes token, backend may blacklist if implemented)
        return GenericResponse.ok("Logout success", "OK");
    }

    @GetMapping("/me")
    public GenericResponse<UserResponse> me(Authentication authentication) {

        // Get logged-in phone from JWT
        String phone = authentication.getName();

        // Fetch user from DB
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Flatten nested DTOs into primitive values for UserResponse
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        String psName = user.getPs() != null ? user.getPs().getPsName() : null;
        String districtName = (user.getPs() != null && user.getPs().getDistrict() != null)
                ? user.getPs().getDistrict().getDistrictName()
                : null;
        String rankName = user.getRank() != null ? user.getRank().getRankName() : null;

        // Map User entity into UserResponse DTO (flat)
        UserResponse userDto = new UserResponse(
                user.getId(),
                user.getName(),
                rankName,
                psName,
                districtName,
                user.getPhone(),
                roleName,
                user.getIsActive()
        );

        return GenericResponse.ok("User details", userDto);
    }
}

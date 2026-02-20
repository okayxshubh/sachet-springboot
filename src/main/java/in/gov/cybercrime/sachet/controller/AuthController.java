package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/register")
    public GenericResponse<String> register(@RequestBody EncryptedRequest request) {
        try {
            String decryptedJson = SachetCrypto.decrypt(request.getPayload());
            RegisterRequest registerRequest = objectMapper.readValue(decryptedJson, RegisterRequest.class);
            String result = authService.register(registerRequest);
            String encryptedData = SachetCrypto.encrypt(result);

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("User registered successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error during registration")
                    .data(null)
                    .build();
        }
    }

    @PostMapping("/login")
    public GenericResponse<String> login(@RequestBody EncryptedRequest request) {
        try {
            String decryptedJson = SachetCrypto.decrypt(request.getPayload());
            LoginRequest loginRequest = objectMapper.readValue(decryptedJson, LoginRequest.class);
            AuthResponse authResponse = authService.login(loginRequest);

            String encryptedData = SachetCrypto.encrypt(objectMapper.writeValueAsString(authResponse));

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Login success")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error during login")
                    .data(null)
                    .build();
        }
    }

    @PostMapping("/refresh")
    public GenericResponse<String> refresh(@RequestBody EncryptedRequest request) {
        try {
            String decryptedJson = SachetCrypto.decrypt(request.getPayload());
            RefreshTokenRequest refreshRequest = objectMapper.readValue(decryptedJson, RefreshTokenRequest.class);
            AuthResponse authResponse = authService.refreshToken(refreshRequest.getRefreshToken());

            String encryptedData = SachetCrypto.encrypt(objectMapper.writeValueAsString(authResponse));

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Token refreshed")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error during token refresh")
                    .data(null)
                    .build();
        }
    }

    @GetMapping("/me")
    public GenericResponse<String> me(Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
            String psName = user.getPs() != null ? user.getPs().getPsName() : null;
            String districtName = (user.getPs() != null && user.getPs().getDistrict() != null)
                    ? user.getPs().getDistrict().getDistrictName()
                    : null;
            String rankName = user.getRank() != null ? user.getRank().getRankName() : null;

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

            String encryptedData = SachetCrypto.encrypt(objectMapper.writeValueAsString(userDto));

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("User details fetched successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error fetching user details")
                    .data(null)
                    .build();
        }
    }


    // NEW: Encrypted request, plain token response
    @PostMapping("/get-token")
    public GenericResponse<String> getToken(@RequestBody String encrypted) {
        try {
            // decrypt the raw string directly
            String decryptedJson = SachetCrypto.decrypt(encrypted);

            // clean up newlines or extra spaces
            decryptedJson = decryptedJson.trim().replaceAll("\\r?\\n", "");

            LoginRequest loginRequest = objectMapper.readValue(decryptedJson, LoginRequest.class);
            AuthResponse authResponse = authService.login(loginRequest);

            // encrypt the full AuthResponse object
            String encryptedData = SachetCrypto.encrypt(objectMapper.writeValueAsString(authResponse));

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Token Fetched Successfully")
                    .data(encryptedData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Invalid phone or password")
                    .data(null)
                    .build();
        }
    }

}

package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.config.JwtUtil;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.masters.PoliceStationMaster;
import in.gov.cybercrime.sachet.masters.RankMaster;
import in.gov.cybercrime.sachet.masters.RoleMaster;
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
    public GenericResponse<String> register(@RequestBody String encrypted) {
        try {
            // decrypt the raw JSON string
            String decryptedJson = SachetCrypto.decrypt(encrypted);

            // convert decrypted JSON to DTO
            RegisterRequest registerRequest = objectMapper.readValue(decryptedJson, RegisterRequest.class);

            // call AuthService to handle full registration
            String result = authService.register(registerRequest);

            // encrypt the result message
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
    public GenericResponse<String> login(@RequestBody String encrypted) {
        try {
            // decrypt the raw string directly
            String decryptedJson = SachetCrypto.decrypt(encrypted);

            // parse into LoginRequest
            LoginRequest loginRequest = objectMapper.readValue(decryptedJson, LoginRequest.class);

            AuthResponse authResponse = authService.login(loginRequest);

            // encrypt the response
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
    public GenericResponse<String> refresh(@RequestBody String refreshToken) {
        try {
            // directly use the raw token
            AuthResponse authResponse = authService.refreshToken(refreshToken);

            // return the new access token in GenericResponse without encryption
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Token refreshed")
                    .data(authResponse.getToken())
                    .build();

        } catch (RuntimeException e) {
            // handle invalid/expired refresh token specifically
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("invalid or expired refresh token")) {
                return GenericResponse.<String>builder()
                        .timestamp(LocalDateTime.now())
                        .status("ERROR")
                        .message("Invalid or expired refresh token")
                        .data(null)
                        .build();
            }

            // fallback for other exceptions
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Server error during token refresh")
                    .data(null)
                    .build();
        }
    }

    // profile info.
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
    @PostMapping("/get-user-token")
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

    // NEW: Stateless global token endpoint
    @PostMapping("/global-token")
    public GenericResponse<String> globalToken() {
        try {
            // generate a JWT for a fixed identifier like "GLOBAL_USER"
            String token = JwtUtil.generateToken("GLOBAL_USER");

            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("OK")
                    .message("Global token generated successfully")
                    .data(token)  // only the raw token
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return GenericResponse.<String>builder()
                    .timestamp(LocalDateTime.now())
                    .status("ERROR")
                    .message("Error generating global token")
                    .data(null)
                    .build();
        }
    }

}

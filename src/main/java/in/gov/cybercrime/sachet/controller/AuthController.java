package in.gov.cybercrime.sachet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.encryption.SachetCrypto;
import in.gov.cybercrime.sachet.entity.User;
import in.gov.cybercrime.sachet.exceptions.ResourceNotFoundException;
import in.gov.cybercrime.sachet.repository.UserRepository;
import in.gov.cybercrime.sachet.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public GenericResponse<String> register(@RequestBody String encrypted) throws Exception {

        String decryptedJson = SachetCrypto.decrypt(encrypted);
        RegisterRequest registerRequest =
                objectMapper.readValue(decryptedJson, RegisterRequest.class);

        String result = authService.register(registerRequest);
        String encryptedData = SachetCrypto.encrypt(result);

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("User registered successfully")
                .data(encryptedData)
                .build();
    }

    @PostMapping("/login")
    public GenericResponse<String> login(@RequestBody String encrypted) throws Exception {

        String decryptedJson = SachetCrypto.decrypt(encrypted);
        LoginRequest loginRequest =
                objectMapper.readValue(decryptedJson, LoginRequest.class);

        AuthResponse authResponse = authService.login(loginRequest);

        String encryptedData =
                SachetCrypto.encrypt(objectMapper.writeValueAsString(authResponse));

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("Login success")
                .data(encryptedData)
                .build();
    }

    @PostMapping("/refresh")
    public GenericResponse<String> refresh(@RequestBody String refreshToken) {

        AuthResponse authResponse = authService.refreshToken(refreshToken);

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("Token refreshed")
                .data(authResponse.getToken())
                .build();
    }

    @GetMapping("/me")
    public GenericResponse<String> me(Authentication authentication) throws Exception {

        String phone = authentication.getName();

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

        String encryptedData =
                SachetCrypto.encrypt(objectMapper.writeValueAsString(userDto));

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("User details fetched successfully")
                .data(encryptedData)
                .build();
    }

    @PostMapping("/get-user-token")
    public GenericResponse<String> getToken(@RequestBody String encrypted) throws Exception {

        String decryptedJson = SachetCrypto.decrypt(encrypted);
        decryptedJson = decryptedJson.trim().replaceAll("\\r?\\n", "");

        LoginRequest loginRequest =
                objectMapper.readValue(decryptedJson, LoginRequest.class);

        AuthResponse authResponse = authService.login(loginRequest);

        String encryptedData =
                SachetCrypto.encrypt(objectMapper.writeValueAsString(authResponse));

        return GenericResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("Token Fetched Successfully")
                .data(encryptedData)
                .build();
    }

    @GetMapping("/check-token")
    public ResponseEntity<String> checkToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("EXPIRED");
        }

        String token = authHeader.substring(7);
        boolean valid = authService.isTokenValid(token);

        if (valid) {
            return ResponseEntity.ok("VALID");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("EXPIRED");
        }
    }
}
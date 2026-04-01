package in.gov.cybercrime.sachet.service;

import in.gov.cybercrime.sachet.dto.AuthResponse;
import in.gov.cybercrime.sachet.dto.ChangePasswordRequest;
import in.gov.cybercrime.sachet.dto.LoginRequest;
import in.gov.cybercrime.sachet.dto.RegisterRequest;
import in.gov.cybercrime.sachet.dto.SendOtpRequest;
import in.gov.cybercrime.sachet.dto.VerifyOtpRequest;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final long OTP_TTL_MILLIS = 5 * 60 * 1000L;
    private static final long VERIFIED_TTL_MILLIS = 10 * 60 * 1000L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RankMasterRepository rankRepository;
    private final PoliceStationMasterRepository policeStationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Long> verifiedOtpStore = new ConcurrentHashMap<>();

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


    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid phone or password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException("User inactive");
        }
        if (Boolean.FALSE.equals(user.getIsApproved())) {
            throw new InvalidCredentialsException("User not approved yet");
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

    // Boolean Reply
    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token);
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
        if (Boolean.FALSE.equals(user.getIsApproved())) {
            throw new InvalidCredentialsException("User not approved yet");
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

    public String changePassword(ChangePasswordRequest request) {
        String phone = normalizePhone(request.getPhone());
        String newPassword = request.getNewPassword() != null ? request.getNewPassword().trim() : "";

        if (newPassword.isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }

        Long verifiedUntil = verifiedOtpStore.get(phone);
        if (verifiedUntil == null || System.currentTimeMillis() > verifiedUntil) {
            verifiedOtpStore.remove(phone);
            throw new InvalidCredentialsException("OTP verification required");
        }

        User user = ensureUserExists(phone);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verifiedOtpStore.remove(phone);
        otpStore.remove(phone);
        return "Password changed successfully";
    }


    /*
    * HELPER METHODS in Service
    * */
    private User ensureUserExists(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.trim().isBlank()) {
            throw new IllegalArgumentException("Phone is required");
        }
        return phone.trim();
    }


    /*
    * OTP Services
    * */
    public String sendOtp(SendOtpRequest request) {
        String phone = normalizePhone(request.getPhone());
        ensureUserExists(phone);

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        long expiresAt = System.currentTimeMillis() + OTP_TTL_MILLIS;

        otpStore.put(phone, new OtpEntry(otp, expiresAt));
        verifiedOtpStore.remove(phone);

        dispatchOtp(phone, otp);
        return "OTP sent successfully";
    }

    public String verifyOtp(VerifyOtpRequest request) {
        String phone = normalizePhone(request.getPhone());
        String otp = request.getOtp() != null ? request.getOtp().trim() : "";

        OtpEntry entry = otpStore.get(phone);
        if (entry == null || System.currentTimeMillis() > entry.expiresAt()) {
            otpStore.remove(phone);
            throw new InvalidCredentialsException("OTP expired or not found");
        }

        if (!entry.otp().equals(otp)) {
            throw new InvalidCredentialsException("Invalid OTP");
        }

        otpStore.remove(phone);
        verifiedOtpStore.put(phone, System.currentTimeMillis() + VERIFIED_TTL_MILLIS);
        return "OTP verified successfully";
    }

    private void dispatchOtp(String phone, String otp) {
        // Replace this with actual SMS provider integration.
        log.info("OTP for {} is {}", phone, otp);
    }

    private record OtpEntry(String otp, long expiresAt) {}
}

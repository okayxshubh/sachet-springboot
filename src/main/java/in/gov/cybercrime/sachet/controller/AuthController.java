package in.gov.cybercrime.sachet.controller;

import in.gov.cybercrime.sachet.dto.*;
import in.gov.cybercrime.sachet.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public GenericResponse<String> register(@RequestBody RegisterRequest request) {
        return GenericResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public GenericResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return GenericResponse.ok("Login success", authService.login(request));
    }
}

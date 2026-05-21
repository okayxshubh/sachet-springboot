package in.gov.cybercrime.sachet.config;

import in.gov.cybercrime.sachet.encryption.EncryptedRequestFilter;
import in.gov.cybercrime.sachet.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.core.annotation.Order;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final EncryptedRequestFilter encryptedRequestFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService,
                          EncryptedRequestFilter encryptedRequestFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.encryptedRequestFilter = encryptedRequestFilter;
    }

    /**
     * Security chain for encrypted endpoints (like /api/crypto/**)
     * No authentication is required, just decrypt payloads.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain cryptoChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/crypto/**");
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // all /api/crypto/** requests are open
        );
        return http.build();
    }

    /**
     * Main API security chain for /api/** endpoints.
     * Master APIs are open (no JWT), admin endpoints require SUPERADMIN JWT.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

                // =========================
                // 1. CORS / PRE-FLIGHT
                // =========================
                // Required for browser-based clients (OPTIONS calls before actual request)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                // =========================
                // 2. PUBLIC AUTH ENDPOINTS
                // =========================
                // These endpoints DO NOT require JWT

                // Login
                .requestMatchers("/api/auth/login").permitAll()

                // Token generation (duplicate login-style endpoint)
                .requestMatchers("/api/auth/get-user-token").permitAll()

                // Token validation (used by frontend to check expiry)
                .requestMatchers("/api/auth/check-token").permitAll()

                // Refresh token
                .requestMatchers("/api/auth/refresh").permitAll()

                // OTP flow (no login required initially)
                .requestMatchers("/api/auth/send-otp").permitAll()
                .requestMatchers("/api/auth/verify-otp").permitAll()


                // =========================
                // 3. PROTECTED AUTH ENDPOINTS  // These REQUIRE valid JWT
                // =========================

                // Get logged-in user details
                .requestMatchers("/api/auth/me").authenticated()

                // Change password (NO JWT)
                .requestMatchers("/api/auth/change-password").permitAll()


                // =========================
                // 4. USER MODULE
                // =========================
                // Public registration
                .requestMatchers("/api/users/register").permitAll()

                // Admin / Nodal actions → require elevated roles
                .requestMatchers("/api/users/approve").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/users/approval-pool").hasAnyRole("ADMIN", "SUPERADMIN")

                // User management → restricted
                .requestMatchers("/api/users/update").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/users/delete").hasAnyRole("ADMIN", "SUPERADMIN")

                // Fetch specific user → authenticated users only
                .requestMatchers("/api/users/get").authenticated()
                .requestMatchers("/api/users/by-rank-active").authenticated()


                // =========================
                // 5. OPEN UTILITY / MASTER APIs  // No authentication required
                // =========================
                .requestMatchers("/api/crypto/**").permitAll()
                .requestMatchers("/api/master/**").permitAll()
                .requestMatchers("/api/masters/**").permitAll()
                .requestMatchers("/api/dashboard/system-summary").permitAll() // Public dashboard stats
//                .requestMatchers("/api/notices/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/notices/templates", "/api/notices/templates/*").authenticated()
                .requestMatchers("/api/notices/templates/detail").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/notices/templates/*").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/notices/templates/update").hasAnyRole("ADMIN", "SUPERADMIN")


                // =========================
                // 6. ADMIN MODULE (GENERIC)
                // =========================
                // Any other admin APIs
                .requestMatchers("/api/admin/**")
                .hasAnyRole("SUPERADMIN", "ADMIN")


                // =========================
                // 7. FALLBACK SECURITY
                // =========================
                // Everything else MUST be authenticated
                .anyRequest().authenticated()
        );

        // Filters for encryption and JWT
        http.addFilterBefore(encryptedRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password encoder bean for hashing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strong
    }

    /**
     * AuthenticationManager bean to expose authentication capabilities.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

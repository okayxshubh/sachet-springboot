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
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow CORS preflight
                .requestMatchers("/api/auth/**").permitAll()           // registration/login open
                .requestMatchers("/api/auth/me").authenticated()  // Requires JWT For Profile Info
                .requestMatchers("/api/auth/get-token").permitAll()    // get token externally
                .requestMatchers("/api/crypto/**").permitAll()         // crypto endpoints open
                .requestMatchers("/api/master/**").permitAll()         // master APIs open (no JWT)
                .requestMatchers("/api/masters/**").permitAll()        // master APIs open
                .requestMatchers("/api/admin/**").hasAnyRole("SUPERADMIN", "ADMIN") // admin endpoints require JWT + These roles
                .anyRequest().authenticated()                           // other endpoints require JWT
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

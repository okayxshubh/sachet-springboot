package in.gov.cybercrime.sachet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    private static final String SECRET = "01234567890123456789012345678901";

    // Separate expiration
    private static final long ACCESS_EXPIRATION_MS = 1000 * 60 * 60 * 8;       // 8 hours
    private static final long REFRESH_EXPIRATION_MS = 1000L * 60 * 60 * 24; // 1 day

    // Only track ACTIVE ACCESS tokens (not refresh)
    private final ConcurrentHashMap<String, String> activeUserKeys = new ConcurrentHashMap<>();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private String generateUserKey() {
        return UUID.randomUUID().toString();
    }

    // =========================
    // ACCESS TOKEN
    // =========================
    public String generateToken(String username, String role) {
        String userKey = generateUserKey();

        // invalidate previous session
        activeUserKeys.put(username, userKey);

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("key", userKey)
                .claim("type", "access")   // IMPORTANT
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ACCESS_EXPIRATION_MS)))
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    public String generateRefreshToken(String username, String role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", "refresh")  // IMPORTANT
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(REFRESH_EXPIRATION_MS)))
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // VALIDATE ACCESS TOKEN
    // =========================
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            String type = claims.get("type", String.class);
            if (!"access".equals(type)) return false;

            String username = claims.getSubject();
            String tokenKey = claims.get("key", String.class);

            return tokenKey != null && tokenKey.equals(activeUserKeys.get(username));

        } catch (JwtException e) {
            return false;
        }
    }

    // =========================
    // VALIDATE REFRESH TOKEN
    // =========================
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            String type = claims.get("type", String.class);
            return "refresh".equals(type); // ONLY check type + expiry

        } catch (JwtException e) {
            return false;
        }
    }

    // =========================
    // COMMON CLAIM EXTRACTION
    // =========================
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
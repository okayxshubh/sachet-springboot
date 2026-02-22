package in.gov.cybercrime.sachet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    // 32-byte secret key
    private static final String SECRET = "01234567890123456789012345678901";
    // Token expiration in ms (5 sec for testing single-token)
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 8; // 8 hours

    // Map to store the latest token key per user
    private final ConcurrentHashMap<String, String> activeUserKeys = new ConcurrentHashMap<>();

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate a unique key for this login
    private String generateUserKey() {
        return UUID.randomUUID().toString();
    }

    // JwtUtil fixed
    public String generateToken(String username, String role) {
        String userKey = generateUserKey();
        activeUserKeys.put(username, userKey); // store for single-token enforcement

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("key", userKey)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(EXPIRATION_MS)))
                .signWith(getSigningKey())
                .compact();
    }

    // Refresh token (do NOT overwrite activeUserKeys)
    public String generateRefreshToken(String username, String role) {
        String userKey = generateUserKey(); // optional separate key for refresh
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("key", userKey)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(EXPIRATION_MS * 24)))
                .signWith(getSigningKey())
                .compact();
    }

    // Validate token against the stored key
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String tokenKey = claims.get("key", String.class);

            // Only valid if token key matches latest key for this user
            return tokenKey != null && tokenKey.equals(activeUserKeys.get(username));
        } catch (JwtException e) {
            return false;
        }
    }

    // Validate refresh token (same logic)
    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    // Extract username from token
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
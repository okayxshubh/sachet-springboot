package in.gov.cybercrime.sachet.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "S@ch3tJWTKey#2026!"; // 16+ chars (better 32+)
    private static final long EXPIRY_MS = 1000 * 60 * 60 * 24; // 24 hours

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

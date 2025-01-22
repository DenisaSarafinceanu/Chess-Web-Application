package Chesllyly.Chesslyly.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateAccessToken(String userEmail) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600000))  // Token expires in 1 hour
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String userEmail) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 604800000))  // 7 days expiration for refresh token
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            // Check if token has expired
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserEmail(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

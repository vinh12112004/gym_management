package com.example.gymmanagement.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * @param base64Secret   Khóa JWT đã được encode Base64, ít nhất 256 bit (32 bytes) trước khi encode.
     * @param expirationMs   Thời gian sống của token (ms).
     */
    public JwtUtil(@Value("${jwt.secret}") String base64Secret,
                   @Value("${jwt.expirationMs}") long expirationMs) {
        // Giải mã Base64 để lấy mảng byte đủ độ dài >=256 bit
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Sinh JWT dựa trên thông tin UserDetails
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(now)
            .setExpiration(expireAt)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Lấy username (subject) từ token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key).build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Kiểm tra token còn hợp lệ so với UserDetails
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = getUsernameFromToken(token);
            return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date exp = Jwts.parserBuilder()
            .setSigningKey(key).build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return exp.before(new Date());
    }
}

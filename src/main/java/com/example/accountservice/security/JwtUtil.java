package com.example.accountservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); }
    public String extractUsername(String token) { return claims(token).getSubject(); }
    public boolean isTokenValid(String token) {
        try { claims(token); return true; }
        catch (Exception ex) { return false; }
    }
    private Claims claims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}

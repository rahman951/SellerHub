package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret.access}")
    private String accessSecretBase64;


    @Value("${jwt.expiration.refresh}")
    private Duration refreshTtl;


    @Value("${jwt.expiration.access}")
    private Duration accessTtl;

    private Key getSignInKey(String base64Secret) {

        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);

        Date issuedDate = new Date();


        long ttlMillis = refreshTtl.toMillis();
        Date expiresDate = new Date(issuedDate.getTime() + ttlMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiresDate)
                .signWith(getSignInKey(accessSecretBase64), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey(accessSecretBase64))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getAllClaimsFromToken(token).get("role").toString();
    }
}

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
import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key accessKey;
    private final Key refreshKey;
    private final TemporalAmount accessTtl;
    private final TemporalAmount refreshTtl;

    public JwtUtil(@Value("${jwt.secret.access}") String accessSecretBase64,
                   @Value("${jwt.secret.refresh}") String refreshSecretBase64,
                   @Value("${jwt.expiration.access}") String accessExp,
                   @Value("${jwt.expiration.refresh}") String refreshExp) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretBase64));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretBase64));
        this.accessTtl = parseTemporal(accessExp);
        this.refreshTtl = parseTemporal(refreshExp);
    }

    public String generateAccessToken(UserDetails principal) {
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Instant now = Instant.now();
        Instant exp = plus(now, accessTtl);
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .addClaims(Map.of("roles", roles))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails principal) {
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Instant now = Instant.now();
        Instant exp = plus(now, refreshTtl);
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .addClaims(Map.of("roles", roles))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims c = parse(token, accessKey);
            return c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims c = parse(token, refreshKey);
            return c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String usernameFromAccess(String token) {
        return parse(token, accessKey).getSubject();
    }

    public String usernameFromRefresh(String token) {
        return parse(token, refreshKey).getSubject();
    }

    public List<String> rolesFromAccess(String token) {
        Object v = parse(token, accessKey).get("roles");
        if (v instanceof List<?> list) return list.stream().map(String::valueOf).toList();
        return List.of();
    }

    private Claims parse(String token, Key key) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private static TemporalAmount parseTemporal(String iso) {
        if (iso.startsWith("P") && !iso.startsWith("PT")) return Period.parse(iso);
        return Duration.parse(iso);
    }

    private static Instant plus(Instant start, TemporalAmount amount) {
        if (amount instanceof Period p) return start.atZone(ZoneOffset.UTC).plus(p).toInstant();
        return start.plus((Duration) amount);
    }
}

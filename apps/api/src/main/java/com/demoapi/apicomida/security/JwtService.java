package com.demoapi.apicomida.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-expiration-seconds:3600}") long accessExpirationSeconds,
            @Value("${app.security.jwt.refresh-expiration-seconds:604800}") long refreshExpirationSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(resolveSecretBytes(secret));
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    public String generateAccessToken(UUID userId, String email) {
        return buildToken(userId, email, accessExpirationSeconds, Map.of("type", "access"));
    }

    public String generateRefreshToken(UUID userId, String email) {
        return buildToken(userId, email, refreshExpirationSeconds, Map.of("type", "refresh"));
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public String extractType(String token) {
        return parse(token).get("type", String.class);
    }

    public Instant extractExpiration(String token) {
        return parse(token).getExpiration().toInstant();
    }

    private String buildToken(UUID userId, String email, long expirationSeconds, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .claim("email", email)
                .signWith(signingKey)
                .compact();
    }

    private byte[] resolveSecretBytes(String secret) {
        try {
            return Decoders.BASE64.decode(secret);
        } catch (DecodingException exception) {
            return sha256(secret);
        }
    }

    private byte[] sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }
}

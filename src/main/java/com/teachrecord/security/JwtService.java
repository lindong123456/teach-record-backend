package com.teachrecord.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String createTeacherToken(long teacherId, String subject) {
        return buildToken(subject, teacherId, 0, false);
    }

    public String createParentToken(long teacherId, long studentId, String subject) {
        return buildToken(subject, teacherId, studentId, true);
    }

    private String buildToken(String subject, long teacherId, long studentId, boolean parent) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .claim("tid", teacherId)
                .claim("sid", studentId)
                .claim("p", parent)
                .signWith(getKey())
                .compact();
    }

    public AppUserPrincipal parseUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Number tid = claims.get("tid", Number.class);
        Number sid = claims.get("sid", Number.class);
        long teacherId = tid != null ? tid.longValue() : 0L;
        long studentId = sid != null ? sid.longValue() : 0L;
        boolean parent = parseParentClaim(claims.get("p"));
        return new AppUserPrincipal(teacherId, studentId, claims.getSubject(), parent);
    }

    /** JWT claim {@code p}: tolerate Boolean, 0/1, or string (some serializers differ). */
    private static boolean parseParentClaim(Object raw) {
        if (Boolean.TRUE.equals(raw)) {
            return true;
        }
        if (raw instanceof Number n) {
            return n.intValue() != 0;
        }
        if (raw instanceof String s) {
            return "true".equalsIgnoreCase(s) || "1".equals(s);
        }
        return false;
    }

    private SecretKey getKey() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            // pad for local dev; production must use 256+ bit secret
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, Math.min(bytes.length, 32));
            for (int i = bytes.length; i < 32; i++) {
                padded[i] = (byte) i;
            }
            bytes = padded;
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}

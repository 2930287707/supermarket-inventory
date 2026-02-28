package com.supermarket.supermarketinventory.security;

import com.supermarket.supermarketinventory.common.BusinessException;
import com.supermarket.supermarketinventory.common.ErrorCode;
import com.supermarket.supermarketinventory.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NICKNAME = "nickname";

    private final JwtProperties jwtProperties;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(SysUser user) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofHours(jwtProperties.getExpireHours()));
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .claim(CLAIM_UID, user.getId())
                .claim(CLAIM_ROLE, user.getRole())
                .claim(CLAIM_NICKNAME, user.getNickname())
                .signWith(getSigningKey())
                .compact();
    }

    public JwtUserInfo parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            JwtUserInfo userInfo = new JwtUserInfo();
            Number userId = claims.get(CLAIM_UID, Number.class);
            userInfo.setUserId(userId == null ? null : userId.longValue());
            userInfo.setUsername(claims.getSubject());
            userInfo.setRole(claims.get(CLAIM_ROLE, String.class));
            userInfo.setNickname(claims.get(CLAIM_NICKNAME, String.class));
            return userInfo;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录已过期，请重新登录");
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

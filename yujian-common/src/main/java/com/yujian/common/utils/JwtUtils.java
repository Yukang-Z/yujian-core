package com.yujian.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具
 */
public final class JwtUtils {

    private static final String SECRET = "yujian-oral-secret-key-2024";
    private static final long EXPIRE = 7200 * 1000L;

    private JwtUtils() {
    }

    public static String createToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<String, Object>(4);
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return Long.valueOf(String.valueOf(userId));
    }

    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}

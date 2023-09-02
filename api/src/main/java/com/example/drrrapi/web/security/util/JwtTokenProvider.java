package com.example.drrrapi.web.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
public class JwtTokenProvider {

    private final String secretKeyFile;
    private long accessTokenValidityInMilliseconds;
    private long refreshTokenValidityInMilliseconds;
    private Map<String, String> keyValues;
    private String secretKey;


    public JwtTokenProvider() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            secretKeyFile = "/Users/jaehong/jwt-secret-key.txt";
        } else {
            secretKeyFile = "C:/secret-key/jwt-secret-key.txt";
        }
    }

    public String createAccessToken(Map<String, Object> claims) {
        log.info("accessTokenValidityInMilliseconds 값:" + accessTokenValidityInMilliseconds);
        return createToken(claims, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        Map<String, Object> claims = new HashMap<>();

        String generatedString = new String(array, StandardCharsets.UTF_8);
        claims.put("random byte", generatedString);
        return createToken(claims, refreshTokenValidityInMilliseconds);
    }


    public String createToken(Map<String, Object> claims, long expirationTimeInMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public Claims decodeJwtPayload(String jwtToken) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(jwtToken);
        return jws.getBody();
    }

    public String getMemberId(String token) {
        Map<String, Object> payloadMap = getPayload(token);
        return (String) payloadMap.get("id");
    }

    @PostConstruct
    private void init() {
        keyValues = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(secretKeyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    keyValues.put(key, value);
                }
            }

            secretKey = keyValues.get("secretKey");
            accessTokenValidityInMilliseconds = Long.parseLong(keyValues.get("accessTokenValidityInMilliseconds"));
            refreshTokenValidityInMilliseconds = Long.parseLong(keyValues.get("accessTokenValidityInMilliseconds"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getPayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            log.error("Error Occurred:" + e.getMessage());
            throw new RuntimeException("유효하지 않은 토큰 입니다");
        }
    }

    public boolean isTokenValid(String token) {
        boolean isTokenInvalid = false;

        // 토큰의 변조 여부 검사

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            // 토큰 변조로 인한 예외 처리
            log.error("Error Occurred: " + e.getMessage());
            isTokenInvalid = true;
        }

        // 토큰 만료 검증

        return !isTokenInvalid;
    }

}

package com.example.drrrapi.auth.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// 60초 동안 생존

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh-token", timeToLive = 60 * 60L) // 3600초 동안 살아 있음
public class AuthenticationToken {
    private String refreshToken;

    @Id
    private Long memberId;

    @Builder
    private AuthenticationToken(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

}

package com.drrr.domain.auth.service;


import com.drrr.domain.auth.entity.AuthenticationToken;
import com.drrr.domain.auth.repository.RedisAuthenticationTokenRepository;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationTokenService {
    private final RedisAuthenticationTokenRepository authenticationTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void register(final RegisterAuthenticationTokenDto registerAuthenticationTokenDto) {
        authenticationTokenRepository.save(AuthenticationToken.builder()
                .memberId(registerAuthenticationTokenDto.memberId)
                .refreshToken(registerAuthenticationTokenDto.refreshToken)
                .build());

    }

    public void remove(final RemoveAuthenticationTokenDto removeAuthenticationTokenDto) {
        final AuthenticationToken authenticationToken = authenticationTokenRepository.findById(
                        removeAuthenticationTokenDto.memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        authenticationTokenRepository.delete(authenticationToken);
    }

    public void logout(final Long memberId, final String accessToken, final Long ttlMillis) {
        authenticationTokenRepository.findById(memberId)
                .ifPresent(authenticationTokenRepository::delete);
        //key : access token, value : "Logout", ttl : access token의 유효시간만큼
        redisTemplate.opsForValue().set(accessToken, "Logout", ttlMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * dto 같은 경우에는 service 내부에 위치시키도록 변경
     *
     * @param memberId
     * @param refreshToken
     */
    @Builder
    public record RegisterAuthenticationTokenDto(Long memberId, String refreshToken) {

    }

    public record RemoveAuthenticationTokenDto(Long memberId) {

    }


}

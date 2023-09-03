package com.drrr.domain.auth.service;


import com.drrr.domain.auth.entity.AuthenticationToken;
import com.drrr.domain.auth.repository.RedisAuthenticationTokenRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationTokenService {
    private final RedisAuthenticationTokenRepository authenticationTokenRepository;

    public void register(RegisterAuthenticationTokenDto registerAuthenticationTokenDto) {
        authenticationTokenRepository.save(AuthenticationToken.builder()
                .memberId(registerAuthenticationTokenDto.memberId)
                .refreshToken(registerAuthenticationTokenDto.refreshToken)
                .build());

    }

    public void remove(RemoveAuthenticationTokenDto removeAuthenticationTokenDto) {
        final AuthenticationToken authenticationToken = authenticationTokenRepository.findById(removeAuthenticationTokenDto.memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        authenticationTokenRepository.delete(authenticationToken);
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

package com.example.drrrapi.auth.service.impl;


import com.example.drrrapi.auth.payload.request.SignInRequest;
import com.example.drrrapi.auth.payload.response.SignInResponse;
import com.example.drrrapi.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService {
    private final ExternalAuthenticationFacade externalAuthenticationFacade;
    private final MemberRepository memberRepository;
    private final IssuanceTokenService issuanceTokenService;


    @Transactional(readOnly = true)
    public SignInResponse execute(SignInRequest signInRequest) {
        final var socialId = externalAuthenticationFacade.execute(signInRequest.getAccessToken(), signInRequest.getProvider());
        final var member = memberRepository.findByProviderId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        final var tokenDto = issuanceTokenService.execute(member.getId());
        return SignInResponse.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}

package com.drrr.auth.service.impl;


import com.drrr.auth.payload.request.SignInRequest;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.service.impl.IssuanceTokenService.IssuanceTokenDto;
import com.drrr.domain.member.service.MemberIdRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService {
    private final MemberIdRetrievalService memberIdRetrievalService;
    private final IssuanceTokenService issuanceTokenService;


    @Transactional(readOnly = true)
    public SignInResponse execute(final SignInRequest signInRequest) {
        final String socialId = signInRequest.providerId();
        final Long memberId = memberIdRetrievalService.findByProviderId(socialId);
        final IssuanceTokenDto tokenDto = issuanceTokenService.execute(memberId);

        return SignInResponse.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}

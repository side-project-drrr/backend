package com.drrr.auth.service.impl;


import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.SignUpResponse;

import com.drrr.domain.category.service.InitializeWeightService;
import com.drrr.domain.member.service.RegisterMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final ExternalAuthenticationFacade externalAuthenticationFacade;
    private final RegisterMemberService registerMemberService;
    private final IssuanceTokenService issuanceTokenService;
    private final InitializeWeightService initializeWeightService;

    public SignUpResponse execute(SignUpRequest signUpRequest) {
        var socialId = externalAuthenticationFacade.execute(signUpRequest.getAccessToken(), signUpRequest.getProvider());
        var memberId = registerMemberService.execute(socialId, signUpRequest.toRegisterMemberDto());
        var issuanceTokenDto = issuanceTokenService.execute(memberId);
        initializeWeightService.initializeCategoryWeight(memberId, signUpRequest.getCategoryIds());


        return SignUpResponse.builder()
                .accessToken(issuanceTokenDto.getAccessToken())
                .refreshToken(issuanceTokenDto.getRefreshToken())
                .build();
    }
}

package com.drrr.auth.service.impl;


import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final ExternalAuthenticationFacade externalAuthenticationFacade;
    private final RegisterMemberService registerMemberService;
    private final IssuanceTokenService issuanceTokenService;

    public SignUpResponse execute(SignUpRequest signUpRequest) {
        var socialId = externalAuthenticationFacade.execute(signUpRequest.getAccessToken(), signUpRequest.getProvider());
        var memberId = registerMemberService.execute(socialId, signUpRequest);
        var issuanceTokenDto = issuanceTokenService.execute(memberId);

        return SignUpResponse.builder()
                .accessToken(issuanceTokenDto.getAccessToken())
                .refreshToken(issuanceTokenDto.getRefreshToken())
                .build();
    }
}

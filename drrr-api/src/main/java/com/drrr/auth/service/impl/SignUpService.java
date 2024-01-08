package com.drrr.auth.service.impl;


import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.IssuanceTokenService.IssuanceTokenDto;
import com.drrr.domain.category.service.InitializeWeightService;
import com.drrr.domain.member.service.RegisterMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignUpService {
    private final RegisterMemberService registerMemberService;
    private final IssuanceTokenService issuanceTokenService;
    private final InitializeWeightService initializeWeightService;

    public SignUpResponse execute(final SignUpRequest signUpRequest) {
        Long memberId = registerMemberService.execute(signUpRequest.toRegisterMemberDto());
        IssuanceTokenDto issuanceTokenDto = issuanceTokenService.execute(memberId);
        initializeWeightService.initializeCategoryWeight(memberId, signUpRequest.categoryIds());

        return SignUpResponse.from(issuanceTokenDto);
    }
}

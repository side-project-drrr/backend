package com.drrr.auth.service.impl;


import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.payload.response.NickStatus;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.auth.service.impl.IssuanceTokenService.IssuanceTokenDto;
import com.drrr.domain.category.service.InitializeWeightService;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.member.service.RegisterMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final RegisterMemberService registerMemberService;
    private final IssuanceTokenService issuanceTokenService;
    private final InitializeWeightService initializeWeightService;
    private final MemberRepository memberRepository;

    @Transactional
    public SignUpResponse execute(final SignUpRequest signUpRequest) {
        Long memberId = registerMemberService.execute(signUpRequest.toRegisterMemberDto());
        IssuanceTokenDto issuanceTokenDto = issuanceTokenService.execute(memberId);
        initializeWeightService.initializeCategoryWeight(memberId, signUpRequest.categoryIds());

        return SignUpResponse.from(issuanceTokenDto);
    }

    @Transactional(readOnly = true)
    public NickStatus checkMemberNicknameDuplication(final String nickname){
        return NickStatus.builder()
                .isDuplicate(memberRepository.existsByNickname(nickname))
                .build();
    }
}

package com.drrr.auth.service.impl;

import com.drrr.auth.entity.Member;
import com.drrr.auth.payload.request.SignUpRequest;
import com.drrr.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterMemberService {
    private final MemberRepository memberRepository;


    @Transactional
    public Long execute(final String socialId, SignUpRequest signUpRequest) {
        memberRepository.findByNicknameOrEmail(signUpRequest.getNickname(), signUpRequest.getEmail())
                .ifPresent((member) -> {
                    throw new IllegalArgumentException("아이디, 닉네임 혹은 이메일이 이미 등록되어 있습니다.");
                });
        var member = Member.builder()
                .email(signUpRequest.getEmail())
                .providerId(socialId)
                .nickname("test")
                .build();
        var savedMember = memberRepository.save(member);
        return savedMember.getId();
    }
}

package com.drrr.domain.member.service;

import com.drrr.core.code.Gender;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterMemberService {
    private final MemberRepository memberRepository;


    @Transactional
    public Long execute(final String socialId, RegisterMemberDto registerMemberDto) {
        memberRepository.findByNicknameOrEmail(registerMemberDto.nickname, registerMemberDto.email)
                .ifPresent((member) -> {
                    throw new IllegalArgumentException("아이디, 닉네임 혹은 이메일이 이미 등록되어 있습니다.");
                });
        var member = Member.builder()
                .email(registerMemberDto.email)
                .providerId(socialId)
                .nickname("test")
                .build();
        var savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Builder
    public record RegisterMemberDto(
            String accessToken,
            String email,
            String nickname,
            String phoneNumber,
            Gender gender,
            String birthYear,
            String provider,
            String providerId,
            String imageUrl
    ) {
    }
}

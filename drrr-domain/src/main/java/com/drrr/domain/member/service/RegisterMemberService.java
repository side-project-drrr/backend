package com.drrr.domain.member.service;

import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterMemberService {
    private final MemberRepository memberRepository;


    @Transactional
    public Long execute(final String socialId, final RegisterMemberDto registerMemberDto) {
        memberRepository.findByNicknameOrEmail(registerMemberDto.nickname, registerMemberDto.email)
                .ifPresent((member) -> {
                    log.error(
                            "RegisterMemberService Class execute(final String socialId, final RegisterMemberDto registerMemberDto) Method IllegalArgumentException Error");
                    if (registerMemberDto.email.equals(member.getEmail())) {
                        throw MemberExceptionCode.DUPLICATE_EMAIL.newInstance();
                    } else if (registerMemberDto.nickname.equals(member.getNickname())) {
                        throw MemberExceptionCode.DUPLICATE_NICKNAME.newInstance();
                    } else {
                        throw MemberExceptionCode.UNKNOWN_ERROR.newInstance();
                    }
                });
        Member member = Member.builder()
                .email(registerMemberDto.email)
                .providerId(socialId)
                .nickname(registerMemberDto.nickname)
                .provider(registerMemberDto.provider)
                .providerId(registerMemberDto.providerId)
                .build();
        var savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Builder
    public record RegisterMemberDto(
            String accessToken,
            String email,
            String nickname,
            String provider,
            String providerId
    ) {
    }
}

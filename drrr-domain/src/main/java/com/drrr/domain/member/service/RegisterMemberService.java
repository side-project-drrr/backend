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
@Transactional
@RequiredArgsConstructor
public class RegisterMemberService {
    private final MemberRepository memberRepository;


    public Long execute(final RegisterMemberDto registerMemberDto) {
        memberRepository.findByEmail(registerMemberDto.email)
                .ifPresent((member) -> {
                    log.error(
                            "RegisterMemberService Class execute(final String socialId, final RegisterMemberDto registerMemberDto) Method IllegalArgumentException Error");
                    if (registerMemberDto.email.equals(member.getEmail())) {
                        //이메일이 중복으로 등록되어 있음
                        throw MemberExceptionCode.DUPLICATE_EMAIL.newInstance();
                    } else if (registerMemberDto.nickname.equals(member.getNickname())) {
                        //닉네임이 중복으로 등록되어 있음
                        throw MemberExceptionCode.DUPLICATE_NICKNAME.newInstance();
                    }
                    throw MemberExceptionCode.UNKNOWN_ERROR.newInstance();
                });

        return memberRepository.save(Member.createMember(registerMemberDto)).getId();
    }

    @Builder
    public record RegisterMemberDto(
            String accessToken,
            String email,
            String nickname,
            String provider,
            String providerId,
            String profileImageUrl,
            boolean isActive
    ) {
    }
}

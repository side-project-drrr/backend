package com.drrr.domain.member.service;

import com.drrr.domain.exception.DomainExceptionCode;
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
                        throw DomainExceptionCode.DUPLICATE_EMAIL.newInstance();
                    }
                    if (registerMemberDto.nickname.equals(member.getNickname())) {
                        //닉네임이 중복으로 등록되어 있음
                        throw DomainExceptionCode.DUPLICATE_NICKNAME.newInstance();
                    }
                    throw DomainExceptionCode.UNKNOWN_ERROR.newInstance();
                });

        return memberRepository.save(registerMemberDto.toEntity()).getId();
    }

    @Builder
    public record RegisterMemberDto(
            String accessToken,
            String email,
            String nickname,
            String provider,
            String providerId,
            String profileImageUrl
    ) {
        public Member toEntity() {
            return Member.builder()
                    .email(email())
                    .nickname(nickname())
                    .provider(provider())
                    .providerId(providerId())
                    .profileImageUrl(profileImageUrl())
                    .isActive(true)
                    .build();
        }
    }
}

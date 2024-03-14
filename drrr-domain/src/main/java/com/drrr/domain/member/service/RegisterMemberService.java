package com.drrr.domain.member.service;

import static com.drrr.domain.exception.DomainExceptionCode.DUPLICATE_EMAIL;
import static com.drrr.domain.exception.DomainExceptionCode.DUPLICATE_NICKNAME;

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
        DUPLICATE_EMAIL.invokeByCondition(memberRepository.existsByEmail(registerMemberDto.email));
        DUPLICATE_NICKNAME.invokeByCondition(memberRepository.existsByNickname(registerMemberDto.nickname));

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

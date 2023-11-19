package com.drrr.domain.member.service;
import com.drrr.core.code.member.Gender;
import com.drrr.core.exception.member.MemberException;
import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.entity.MemberRole;
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
                    log.error("RegisterMemberService Class execute(final String socialId, final RegisterMemberDto registerMemberDto) Method IllegalArgumentException Error");
                    if(registerMemberDto.email.equals(member.getEmail())){
                        throw new MemberException(MemberExceptionCode.DUPLICATE_EMAIL.getCode(), MemberExceptionCode.DUPLICATE_EMAIL.getMessage());
                    }else if(registerMemberDto.nickname.equals(member.getNickname())){
                        throw new MemberException(MemberExceptionCode.DUPLICATE_NICKNAME.getCode(), MemberExceptionCode.DUPLICATE_NICKNAME.getMessage());
                    }else{
                        throw new MemberException(MemberExceptionCode.UNKNOWN_ERROR.getCode(), MemberExceptionCode.UNKNOWN_ERROR.getMessage());
                    }
                });
        Member member = Member.builder()
                .email(registerMemberDto.email)
                .providerId(socialId)
                .nickname(registerMemberDto.nickname)
                .phoneNumber(registerMemberDto.phoneNumber)
                .gender(registerMemberDto.gender)
                .provider(registerMemberDto.provider)
                .providerId(registerMemberDto.providerId)
                .imageUrl(registerMemberDto.imageUrl)
                .role(MemberRole.USER)
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

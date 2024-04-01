package com.drrr.domain.member.service;

import static com.drrr.domain.exception.DomainExceptionCode.DUPLICATE_NICKNAME;
import static com.drrr.domain.exception.DomainExceptionCode.EMAIL_DUPLICATE_EXCEPTION;

import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberProfileUpdateService {
    private final MemberRepository memberRepository;

    public void updateMemberProfile(final Long memberId, final String nickname, final String email){
        if(Objects.nonNull(email)){
            EMAIL_DUPLICATE_EXCEPTION.invokeByCondition(memberRepository.existsByEmailFromOthers(memberId, email));
        }
        if(Objects.nonNull(nickname)){
            DUPLICATE_NICKNAME.invokeByCondition(memberRepository.existsByNicknameFromOthers(memberId, nickname));
        }

        memberRepository.updateMemberProfile(memberId, nickname, email);
    }
}

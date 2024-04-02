package com.drrr.domain.member.service;

import static com.drrr.domain.exception.DomainExceptionCode.DUPLICATE_NICKNAME;
import static com.drrr.domain.exception.DomainExceptionCode.EMAIL_DUPLICATE_EXCEPTION;

import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberProfileUpdateService {
    private final MemberRepository memberRepository;

    public void updateMemberProfile(final Long memberId, final String nickname, final String email){
        EMAIL_DUPLICATE_EXCEPTION.invokeByCondition(memberRepository.existsByEmailFromOthers(memberId, email));
        DUPLICATE_NICKNAME.invokeByCondition(memberRepository.existsByNicknameFromOthers(memberId, nickname));

        memberRepository.updateMemberProfile(memberId, nickname, email);
    }
}

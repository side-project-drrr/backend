package com.drrr.member.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.dto.MemberDto;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExternalMemberService {
    private final MemberRepository memberRepository;

    public MemberDto execute(final Long memberId) {
        boolean isActive = memberRepository.findActiveByMemberId(memberId);

        if (isActive) {
            return memberRepository.findMemberProfile(memberId);
        }

        throw DomainExceptionCode.MEMBER_ACCOUNT_DEACTIVATED.newInstance();
    }
}

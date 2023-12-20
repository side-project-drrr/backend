package com.drrr.domain.member.service;

import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnregisterMemberService {
    private final MemberRepository memberRepository;

    public void unregisterMember(final Long memberId) {
        memberRepository.deleteById(memberId);
    }
}

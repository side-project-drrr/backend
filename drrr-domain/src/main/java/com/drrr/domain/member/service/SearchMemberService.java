package com.drrr.domain.member.service;


import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchMemberService {
    private final MemberRepository memberRepository;

    public Member execute(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다. memberId -> {}", memberId);
                    return DomainExceptionCode.MEMBER_NOT_FOUND.newInstance();
                });
    }
}

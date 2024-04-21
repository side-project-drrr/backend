package com.drrr.domain.member.service;


import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.common.MemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchMemberService {
    private final MemberQueryService memberQueryService;

    public Member execute(Long memberId) {
        return memberQueryService.getMemberById(memberId);
    }
}

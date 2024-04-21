package com.drrr.domain.member.service;


import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.common.MemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberIdRetrievalService {
    private final MemberQueryService memberQueryService;

    /**
     * 외부 인증의 회원 아이디 값으로 사용자를 조회함
     */
    public Long findByProviderId(final String userSocialId) {
        final Member member = memberQueryService.getMemberByProviderId(userSocialId);
        return member.getId();
    }

}

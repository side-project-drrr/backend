package com.drrr.domain.member.service;


import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberIdRetrievalService {
    private final MemberRepository memberRepository;

    /**
     * 외부 인증의 회원 아이디 값으로 사용자를 조회함
     */
    public Long findByProviderId(final String userSocialId) {
        final Member member = memberRepository.findByProviderId(userSocialId)
                .orElseThrow(() -> {
                    log.error(
                            "MemberIdRetrievalService Class findByProviderId(final String userSocialId) Method IllegalArgumentException Error");
                    return DomainExceptionCode.UNREGISTERED_MEMBER.newInstance();
                });
        return member.getId();
    }

}

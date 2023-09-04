package com.drrr.domain.member.service;


import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberIdRetrievalService {
    private final MemberRepository memberRepository;

    /**
     * 외부 인증의 회원 아이디 값으로 사용자를 조회함
     *
     * @param userSocialId
     * @return
     */
    public Long findByProviderId(String userSocialId) {
        final Member member = memberRepository.findByProviderId(userSocialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return member.getId();
    }

}

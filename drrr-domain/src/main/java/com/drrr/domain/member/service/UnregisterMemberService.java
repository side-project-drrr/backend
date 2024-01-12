package com.drrr.domain.member.service;

import com.drrr.domain.category.repository.CategoryWeightRepository;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.log.repository.MemberPostHistoryRepository;
import com.drrr.domain.log.repository.MemberPostLogRepository;
import com.drrr.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnregisterMemberService {
    private final MemberRepository memberRepository;

    @Transactional
    //해당 사용자가 좋아요 누른 것도 삭제해줘야 하는가
    public void unregisterMember(final Long memberId) {
        memberRepository.updateUnregisterMember(memberId);
    }
}

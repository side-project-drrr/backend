package com.drrr.domain.category.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MemberViewWeightService {
    private final TechBlogPostRepository techBlogPostRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자가 특정 게시물을 읽었을 때 그 게시물에 대한 가중치 증가
     */
    public Member increaseMemberViewPost(final Long memberId, final Long postId, final List<Long> categoryIds) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다.");
                    log.error("postId -> {}", +postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다.");
                    log.error("memberId -> {}", memberId);
                    return DomainExceptionCode.MEMBER_NOT_FOUND.newInstance();
                });

        //조회수 증가
        post.increaseViewCount();

        return member;
    }
}

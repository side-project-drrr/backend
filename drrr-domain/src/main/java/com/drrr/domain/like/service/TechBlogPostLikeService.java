package com.drrr.domain.like.service;

import com.drrr.core.exception.like.TechBlogPostLikeExceptionCode;
import com.drrr.core.exception.member.MemberExceptionCode;
import com.drrr.core.exception.techblog.TechBlogExceptionCode;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TechBlogPostLikeService {
    private final TechBlogPostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final TechBlogPostRepository techBlogPostRepository;

    public void addPostLike(final Long memberId, final Long postId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다.");
                    log.error("memberId -> {}", memberId);
                    return MemberExceptionCode.MEMBER_NOT_FOUND.newInstance();
                });

        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다.");
                    log.error("postId -> {}", postId);
                    return TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });
        
        postLikeRepository.findByPostIdAndMemberId(memberId, postId)
                .ifPresentOrElse((__)->{
                    log.error("memberId -> {}", memberId);
                    log.error("postId -> {}", postId);
                    log.error("사용자가 하나의 게시물에 중복으로 좋아요를 눌렀습니다.");
                    throw TechBlogPostLikeExceptionCode.DUPLICATE_LIKE.newInstance();
                }, ()->{
                    final TechBlogPostLike like = TechBlogPostLike.builder()
                            .member(member)
                            .post(post)
                            .build();
                    postLikeRepository.save(like);
                    post.increaseLikeCount();
                });
    }

    public void deletePostLike(final Long memberId, final Long postId) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다.");
                    log.error("postId -> {}", postId);
                    return TechBlogExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);

        post.decreaseLikeCount();
    }
}

package com.drrr.domain.like.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.like.entity.TechBlogPostLike;
import com.drrr.domain.like.repository.TechBlogPostLikeRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.service.SearchMemberService;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final TechBlogPostRepository techBlogPostRepository;
    private final SearchMemberService searchMemberService;

    public void addPostLike(final Long memberId, final Long postId) {
        final Member member = searchMemberService.execute(memberId);

        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다.");
                    log.error("postId -> {}", postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        postLikeRepository.findByPostIdAndMemberId(memberId, postId)
                .ifPresentOrElse((postLike) -> {
                    log.error("사용자가 하나의 게시물에 중복으로 좋아요를 눌렀습니다. memberId -> {}, postId ->{} ", memberId, postId);
                    throw DomainExceptionCode.DUPLICATE_LIKE.newInstance();
                }, () -> {
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
                    log.error("기술블로그를 찾을 수 없습니다. postId -> {}", postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        postLikeRepository.findByPostIdAndMemberId(memberId, postId)
                .ifPresentOrElse((postLike) -> {
                    postLikeRepository.deleteByMemberIdAndPostId(memberId, postId);
                    post.decreaseLikeCount();
                }, () -> {
                    log.error("사용자가 하나의 게시물에 중복으로 싫어요를 눌렀습니다. memberId -> {}, postId ->{} ", memberId, postId);
                    throw DomainExceptionCode.DUPLICATE_DISLIKE.newInstance();
                });
    }

    public Set<Long> findLikedPostIdsSet(final Long memberId, final List<Long> postIds) {
        final List<TechBlogPostLike> memberLikedPosts = findMemberLikedPosts(memberId, postIds);

        return memberLikedPosts.stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());
    }

    public List<TechBlogPostLike> findMemberLikedPosts(final Long memberId, final List<Long> postIds) {
        return postLikeRepository.findByMemberIdAndPostIdIn(memberId, postIds);
    }
}

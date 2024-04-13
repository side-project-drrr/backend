package com.drrr.domain.techblogpost.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UnsignedMemberPostService {
    private final TechBlogPostRepository techBlogPostRepository;

    public void increaseViewCount(final Long postId) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다 -> postId: {}", postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        //조회수 증가
        post.increaseViewCount();
    }
}

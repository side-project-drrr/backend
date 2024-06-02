package com.drrr.techblogpost.service;

import com.drrr.domain.exception.DomainExceptionCode;
import com.drrr.domain.techblogpost.entity.TechBlogPost;
import com.drrr.domain.techblogpost.event.RedisPostsEventListener.IncreaseViewEvent;
import com.drrr.domain.techblogpost.repository.TechBlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UnsignedMemberPostReadExternalService {
    private final TechBlogPostRepository techBlogPostRepository;
    private final ApplicationEventPublisher publisher;

    public void increaseViewCount(final Long postId) {
        final TechBlogPost post = techBlogPostRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> {
                    log.error("기술블로그를 찾을 수 없습니다 -> postId: {}", postId);
                    return DomainExceptionCode.TECH_BLOG_NOT_FOUND.newInstance();
                });

        //조회수 증가
        post.increaseViewCount();
        publisher.publishEvent(new IncreaseViewEvent(postId));
    }
}
